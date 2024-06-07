package com.papum.homecookscompanion.view.edit.scan

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityShops
import com.papum.homecookscompanion.view.products.ProductResultViewModel
import com.papum.homecookscompanion.view.products.ProductResultViewModelFactory
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


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

	// uri of image taken by camera intent
	private lateinit var uriCamera: Uri



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
		view.findViewById<ImageButton>(R.id.scan_btn_scan).setOnClickListener { _ ->
			pickFileAndScan()
		}

		view.findViewById<ImageButton>(R.id.scan_btn_camera).setOnClickListener { _ ->
			launcherCameraWithPermission.launch(Manifest.permission.CAMERA)
		}

		view.findViewById<ImageButton>(R.id.scan_btn_info).setOnClickListener { _ ->
			dialogInfoImage()
		}

		view.findViewById<ImageButton>(R.id.scan_btn_confirm).setOnClickListener { _ ->
			if(viewModel.selectedShop.value == null) {
				showErrorMissingShop()
			} else if(viewModel.receiptItems.value == null) {
				showErrorMissingReceipt()
			} else {
				viewModel.saveAllRecognizedTextAssociations()
				viewModel.addAllAssociated_toInventoryAndPurchases()
				showSuccessReceiptAdded()
				navController.navigateUp()
			}
		}

		/* observers */

		// typed shop brand
		viewModel.getMatchingShops().observe(viewLifecycleOwner) { shopsBrands ->
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

	private fun dialogInfoImage() {
		AlertDialog.Builder(context).apply {
			setMessage(R.string.receipt_info)
			setTitle(R.string.receipt_info_title)

			with(create()) {
				show()
			}
		}
	}

	private fun showErrorCamera() {
		Log.e(TAG,"Generical camera error")
		Toast.makeText(context, "ERROR: Camera couldn't be launched", Toast.LENGTH_LONG)
			.show()
	}
	private fun showErrorFileCreation() {
		Log.e(TAG,"Couldn't create a file")
		Toast.makeText(context, "ERROR: Couldn't create file", Toast.LENGTH_LONG)
			.show()
	}
	private fun showErrorCameraPermission() {
		Log.e(TAG,"not granted camera permission")
		Toast.makeText(context, "ERROR: Camera permission wasn't granted", Toast.LENGTH_LONG)
			.show()
	}
	private fun showErrorMissingImage() {
		Toast.makeText(context, "ERROR: Image wasn't picked", Toast.LENGTH_LONG)
			.show()
	}
	private fun showErrorMissingReceipt() {
		Toast.makeText(requireContext(),"Select a receipt picture.", Toast.LENGTH_LONG)
			.show()
		Log.e(TAG, "Select a shop from the suggestions, in order to add this receipt.")
	}
	private fun showErrorMissingShop() {
		Toast.makeText(requireContext(),
			"Select a shop from the suggestions, in order to add this receipt.",
			Toast.LENGTH_LONG
		).show()
		Log.e(TAG, "Select a shop from the suggestions, in order to add this receipt.")
	}
	private fun showSuccessCamera() {
		Toast.makeText(requireContext(), "Picture taken", Toast.LENGTH_SHORT)
			.show()
		Log.d(TAG, "Picture taken!")
	}
	private fun showSuccessImagePicked(uri: Uri?) {
		Toast.makeText(requireContext(), "Receipt image picked, uri: $uri", Toast.LENGTH_SHORT)
			.show()
		Log.d(TAG, "image picked")
	}
	private fun showSuccessReceiptAdded() {
		Toast.makeText(requireContext(),"Receipt successfully added!",	Toast.LENGTH_SHORT)
			.show()
		Log.d(TAG, "Receipt successfully added!")
	}

	/* camera */


	@Throws(IOException::class)
	private fun createImageFile(): File {
		// Create an image file name
		val timeStamp: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))

		val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
			?: throw IOException("External storage not available")

		return File.createTempFile(
			"JPEG_${timeStamp}_", /* prefix */
			".jpg", /* suffix */
			storageDir /* directory */
		)
	}

	private fun _cameraIntent() {
		Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
			// Ensure that there's a camera activity to handle the intent
			takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
				// Create the File where the photo should go
				val photoFile: File? = try {
					createImageFile()
				} catch (ex: IOException) {
					showErrorFileCreation()
					null
				}
				// Continue only if the File was successfully created
				photoFile?.also {
					uriCamera = FileProvider.getUriForFile(
						requireContext(),
						getString(R.string.file_provider_authority),
						it
					)
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriCamera)
					launcherCameraIntent.launch(takePictureIntent)
				}
			}
		}
	}


	/* Image processing */

	/**
	 * Parse the recognized text, coupling the i-th found product with the i-th found price.
	 */
	private fun processImage(uri: Uri?) {

		if(uri == null) {
			showErrorMissingImage()
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
		launcherPicker.launch("image/*")
		//var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
		//chooseFile.setType("*/*")
		//chooseFile = Intent.createChooser(chooseFile, "Choose a file")
		//startActivityForResult(chooseFile, PICKFILE_RESULT_CODE)


	}


	/* launchers */

	private val launcherCameraWithPermission = registerForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { isGranted ->
		if (isGranted) {
			Log.d(TAG,"granted camera permission")
			_cameraIntent()
		} else {
			showErrorCameraPermission()
		}
	}

	private val launcherCameraIntent: ActivityResultLauncher<Intent> =
		registerForActivityResult(
			ActivityResultContracts.StartActivityForResult()
		) { result ->
			if (result.resultCode == Activity.RESULT_OK) {
				showSuccessCamera()
				processImage(uriCamera)
			}
		}

	private val launcherPicker: ActivityResultLauncher<String> =
		registerForActivityResult(
			ActivityResultContracts.GetContent()
		) { uri: Uri? ->
			showSuccessImagePicked(uri)
			processImage(uri)
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