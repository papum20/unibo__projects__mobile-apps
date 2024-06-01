package com.papum.homecookscompanion.view.edit.scan

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text.Line
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityShops
import com.papum.homecookscompanion.view.products.ProductResultViewModel
import com.papum.homecookscompanion.view.products.ProductResultViewModelFactory
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentScanReceipt.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentScanReceipt
	: Fragment(R.layout.fragment_scan_receipt),
	ScanAdapter.IListenerOnClickReceiptEntry
{

	private var selectedProductIndex: Int? = null

	private val viewModel: ScanViewModel by viewModels {
		ScanViewModelFactory(
			Repository(requireActivity().application)
		)
	}
	private val viewModel_selectProduct: ProductResultViewModel by viewModels {
		ProductResultViewModelFactory(requireActivity())
	}
	private lateinit var navController: NavController
	private val adapter = ScanAdapter(mutableListOf(), this)

	private val launcher: ActivityResultLauncher<String> =
		registerForActivityResult(
			ActivityResultContracts.GetContent()
		) { uri: Uri? ->
			Log.i(TAG, "Selected file: $uri")
			processImage(uri)
		}


	override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_scan_receipt, container, false)
	}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		navController = findNavController()

		/* input suggestions */
		// shop brand
		val adapterShopsBrands = ArrayAdapter(
			requireContext(),
			android.R.layout.simple_dropdown_item_1line,
			mutableListOf<EntityShops>()
		)
		view.findViewById<AutoCompleteTextView>(R.id.scan_shop_brand_et).apply {
			setAdapter(adapterShopsBrands)
			threshold = AUTOCOMPLETE_SHOP_BRAND_THRESHOLD

			doOnTextChanged { text, start, before, count ->
				if(viewModel.selectedShop.value != null)
					Log.d(TAG, "Shop set to null.")
				viewModel.typedShop.value		= text.toString()
				viewModel.selectedShop.value	= null
			}

			onItemClickListener =
				AdapterView.OnItemClickListener { parent, view, position, id ->
					viewModel.selectedShop.value = adapterShopsBrands.getItem(position)
					Log.i(TAG, "Selected shop: ${adapterShopsBrands.getItem(position).toString()}")
				}
		}

		/* Recycler */
		val recycler = view.findViewById<RecyclerView>(R.id.scan_recycler_view)
		recycler.adapter = adapter
		recycler.layoutManager = LinearLayoutManager(context)

		/* UI listeners */
		view.findViewById<Button>(R.id.scan_btn_scan).setOnClickListener { _ ->
			pickFileAndScan()
		}

		view.findViewById<Button>(R.id.scan_btn_confirm).setOnClickListener { _ ->
			if(viewModel.selectedShop.value == null) {
				displayMissingShop()
			} else if(viewModel.receiptItems.value == null) {
				displayMissingReceipt()
			} else {
				viewModel.saveAllRecognizedTextAssociations()
				viewModel.addAllAssociated_toInventoryAndPurchases()
				displayReceiptAdded()
				navController.navigateUp()
			}
		}

		/* observers */

		// typed shop brand
		viewModel.getShops_matchingBrand().observe(viewLifecycleOwner) { shopsBrands ->
			adapterShopsBrands.clear()
			adapterShopsBrands.addAll(shopsBrands)
			adapterShopsBrands.notifyDataSetChanged()
		}

		// fetched products associations
		viewModel.getRecognizedProducts().observe(viewLifecycleOwner) { products_withRecognizedAssociations ->
			Log.d(TAG, "Recognized: " + products_withRecognizedAssociations.joinToString{it.product.id.toString()} )
			viewModel.setAllAssociations(products_withRecognizedAssociations)
			viewModel.receiptItems.value?.let { adapter.updateItems(it) }
		}

		// selected product association
		viewModel_selectProduct.selectedProduct.observe(viewLifecycleOwner) { selectedProduct ->
			if(selectedProductIndex != null && selectedProduct != null) {
				viewModel.receiptItems.value?.get(selectedProductIndex!!)?.let { oldItem ->
					viewModel.setAssociation(oldItem, selectedProduct)
					adapter.updateItem(selectedProductIndex!!, oldItem)
					selectedProductIndex = null
					viewModel_selectProduct.reset()
				}
			}
		}

    }


	/* Views */

	private fun displayMissingImage() {
		Toast.makeText(context, "ERROR: Image wasn't picked", Toast.LENGTH_LONG)
			.show()
	}

	private fun displayMissingReceipt() {
		Toast.makeText(
			requireContext(),
			"Select a receipt picture.",
			Toast.LENGTH_LONG
		).show()
		Log.i(TAG, "Select a shop from the suggestions, in order to add this receipt.")
	}
	private fun displayMissingShop() {
		Toast.makeText(
			requireContext(),
			"Select a shop from the suggestions, in order to add this receipt.",
			Toast.LENGTH_LONG
		).show()
		Log.i(TAG, "Select a shop from the suggestions, in order to add this receipt.")
	}
	private fun displayReceiptAdded() {
		Toast.makeText(
			requireContext(),
			"Receipt successfully addedd!",
			Toast.LENGTH_SHORT
		).show()
		Log.i(TAG, "Receipt successfully added!")
	}


	/* Image processing */

	/**
	 * Parse the recognized text, coupling the i-th found product with the i-th found price.
	 */
	private fun processImage(uri: Uri?) {

		if(uri == null) {
			displayMissingImage()
			return
		}

		val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

		try {
			val image = InputImage.fromFilePath(requireContext(), uri)

			recognizer.process(image)
				.addOnSuccessListener { visionText ->

					viewModel.processImage(visionText)
					viewModel.receiptItems.value?.let { adapter.updateItems(it) }

				}
				.addOnFailureListener { e ->
					// Task failed with an exception
				}


		} catch (e: IOException) {
			e.printStackTrace()
		}
	}

	private fun pickFileAndScan() {
		launcher.launch("image/*")
		//var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
		//chooseFile.setType("*/*")
		//chooseFile = Intent.createChooser(chooseFile, "Choose a file")
		//startActivityForResult(chooseFile, PICKFILE_RESULT_CODE)


	}


	/* ScanAdapter.IListenerOnClickSelectProduct */

	override fun onClickSelectProduct(position: Int) {
		selectedProductIndex = position
		navController.navigate(
			FragmentScanReceiptDirections.actionFragmentScanReceiptToProductsWithResult()
		)
	}

	override fun onClickRemove(position: Int) {
		adapter.deleteItem(position)
	}



    companion object {

		private const val TAG = "SCAN"

		private val AUTOCOMPLETE_SHOP_BRAND_THRESHOLD = 1

		/**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        @JvmStatic
        fun newInstance() =
            FragmentScanReceipt()
    }
}