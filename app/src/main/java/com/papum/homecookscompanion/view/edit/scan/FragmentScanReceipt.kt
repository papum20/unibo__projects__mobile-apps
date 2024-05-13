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
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleObserver
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
			ActivityResultContracts.GetContent(),
			ActivityResultCallback<Uri?>() { uri: Uri? ->
				Log.i("FILE", "$uri")
				processImage(uri)
			}
		)


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
		view.findViewById<AutoCompleteTextView>(com.papum.homecookscompanion.R.id.scan_shop_brand_et).apply {
			setAdapter(adapterShopsBrands)
			threshold = AUTOCOMPLETE_SHOP_BRAND_THRESHOLD

			doOnTextChanged { text, start, before, count ->
				if(viewModel.selectedShop.value != null)
					Log.d(LOG_TAG_SCAN, "Shop set to null.")
				viewModel.typedShop.value		= text.toString()
				viewModel.selectedShop.value	= null
			}

			onItemClickListener =
				AdapterView.OnItemClickListener { parent, view, position, id ->
					with(adapterShopsBrands) {
						viewModel.selectedShop.value = adapterShopsBrands.getItem(position)
						Log.i(LOG_TAG_SCAN, "Selected shop: ${adapterShopsBrands.getItem(position).toString()}")
					}
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
			// TODO: 
		}

		/* observers */

		// typed shop brand
		viewModel.getShops_matchingBrand().observe(viewLifecycleOwner) { shopsBrands ->
			adapterShopsBrands.clear()
			adapterShopsBrands.addAll(shopsBrands)
			adapterShopsBrands.notifyDataSetChanged()
		}

		// fetched products associations
		viewModel.getRecognizedProducts().observe(viewLifecycleOwner) { recognizedPerEachProduct ->
			Log.d("RECOGNIZED", recognizedPerEachProduct.joinToString{it.product.id.toString()} )
			viewModel.receiptItems.value?.let { items ->

				recognizedPerEachProduct.forEach { recognizedPerProduct ->
					recognizedPerProduct.recognized.forEach { recognized ->
						// if scan item has not an association yet, search one in the data from db
						items.find {
								it.recognizedProduct == recognized.recognizedText
							}?.let { item ->
								item.product = recognizedPerProduct.product.toString()
						}
					}
				}
				adapter.updateItems(items)
			}
		}

		// selected product association
		viewModel_selectProduct.selectedProduct.observe(viewLifecycleOwner) { selectedProduct ->
			selectedProductIndex?.let { position ->
				viewModel.receiptItems.value?.get(position)?.let { oldItem ->
					adapter.updateItem(position, oldItem.apply {
						product = selectedProduct?.name ?: DFLT_ITEM
					})
					selectedProductIndex = null
				}
			}
		}

    }


	/**
	 * Parse the recognized text, coupling the i-th found product with the i-th found price.
	 */
	private fun processImage(uri: Uri?) {

		if(uri == null) {
			Toast.makeText(context, "ERROR: Image wasn't picked", Toast.LENGTH_LONG)
				.show()
			return
		}

		val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

		try {
			val image = InputImage.fromFilePath(requireContext(), uri)

			recognizer.process(image)
				.addOnSuccessListener { visionText ->

					Log.d(LOG_TAG_SCAN, visionText.text)

					val recognizedLines = mutableListOf<Line>()
					for (block in visionText.textBlocks)
						for (line in block.lines)
							recognizedLines.add(line)

					val lines = groupBlocksInLines(recognizedLines)
					updateReceiptItems(lines)
					viewModel.receiptItems.value?.let { adapter.updateItems(it) }

					Log.d(LOG_TAG_SCAN, lines.joinToString("\n") {
							it.joinToString("\t") {
								//"ITEM: ${it.boundingBox}, ${it.boundingBox!!.centerY()}, ${it.text}"
								"{${it.text}}"
							}
						}
					)

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

	/** Group lines with same Y, and sort by X.
	 */
	private fun groupBlocksInLines(blocks: List<Line>): MutableList<List<Line>> {

		// sort by height
		val blocks_sorted = blocks.sortedBy { it.boundingBox?.centerY() }
		//Log.i(LOG_TAG_SCAN + "2", recognizedLines_sorted.joinToString("\n") { it.text })

		val lines = mutableListOf<List<Line>>()
		var i = 0
		while(i < blocks_sorted.size) {
			var j = i + 1	// sublist(i, j) excluding j is always on same line
			// && instead of 'and' is important: it's short-circuit/lazy
			while( (j + 1 <= blocks_sorted.size) &&
				areBlocksAtSameY(blocks_sorted.subList(i, j+1))
			) {
				j++
			}
			lines.add( blocks_sorted.subList(i, j).sortedBy { it.boundingBox?.left } )
			i = j
		}

		return lines
	}

	/**
	 * Checks that all recognized blocks are at the "same" Y (so in same line).
	 * Specifically, checks that, for each pair of input blocks, the Y-coordinate of the first
	 * falls in the range of the minimum and maximum Y of the second.
	 */
	private fun areBlocksAtSameY(blocksWithTexts: List<Line>): Boolean {

		// check elements not null (note that || is lazy, "or" not)
		for(i in blocksWithTexts.indices) {
			if( (blocksWithTexts[i].cornerPoints == null) ||
				(blocksWithTexts[i].boundingBox == null) ||
				(blocksWithTexts[i].cornerPoints!!.size < 4)
			)
				return false
		}

		for(i in blocksWithTexts.indices) {
			for(j in i+1..<blocksWithTexts.size) {
				val yi_min = blocksWithTexts[j].boundingBox!!.top
				val yi_max = blocksWithTexts[j].boundingBox!!.bottom
				val yj_min = blocksWithTexts[j].boundingBox!!.top
				val yj_max = blocksWithTexts[j].boundingBox!!.bottom
				if ((blocksWithTexts[i].boundingBox!!.centerY() !in yi_min..yi_max) or
					(blocksWithTexts[j].boundingBox!!.centerY() !in yj_min..yj_max)
				)
					return false
			}
		}
		return true
	}

	/**
	 * Extract float from a string recognized with REGEX_PRICE or REGEX_PRICE_ONLY.
	 */
	private fun regexPrice_toFloat(price: String): Float {
		return price.replace(" ", "").replace(",", ".").toFloat()
	}
	/**
	 * Extract float from a string recognized with REGEX_WEIGHT_KG.
	 */
	private fun regexWeight_toFloat(price: String): Float {
		return price.replace(" ", "").replace(",", ".").toFloat()
	}
	/**
	 * Extract float from a string recognized with REGEX_PIECES.
	 */
	private fun regexPieces_toFloat(price: String): Float {
		return price.replace(" ", "").toFloat()
	}

	/**
	 * Update receiptItems.
	 * Extract products names and prices, with regex;
	 * in case of repeated products/prices for a line, take the first one.
	 * Also, save data found in rows for price/weight or price/pieces,
	 * to try to add it to the correct product.

	 */
	private fun updateReceiptItems(lines: List<List<Line>>) {

		val newReceiptItems = mutableListOf<ScanModel>()

		var last_pieces: Float?		= null
		var last_price_kg: Float?	= null
		var last_price_pieces: Float? = null
		var last_weight_kg: Float?	= null

		for(i in lines.indices) {
			var product: String?	= null
			var price: Float?		= null
			for(block in lines[i]) {

				if(REGEX_PRICE_ONLY.matches(block.text)) {
					if (price == null)
					// reformat to float - if , was used, replace with . for convenience
						price = regexPrice_toFloat(block.text)
				} else if(!REGEX_TAXES_ONLY.matches(block.text) && REGEX_PRODUCT.matches(block.text)) {
					if (product == null)
						product = block.text
				}
			}

			// if row didn't contain a price, maybe it contains price/weight or price/pieces
			if( product != null && price == null ) {

				// give priority to weight over pieces (as it's more precise)
				REGEX_PRICE.find(product)?.value?.let { price_unit ->

					REGEX_WEIGHT_KG.find(product!!)?.value?.let { weight_kg ->
						/* In case, override last saved, as it was probably found too soon and
						not matched to any product. */
						last_pieces		= null
						last_price_kg	= regexPrice_toFloat(price_unit)
						last_price_pieces = null
						last_weight_kg	= regexWeight_toFloat(weight_kg)
						// if it was a weight/pieces, don't save as product
						product = null
					}
						?: kotlin.run {
							// don't search pieces inside price and weight (which would match)
							REGEX_PIECES.find(
								product!!.replace(REGEX_PRICE, "")
									.replace(REGEX_WEIGHT_KG, "")
							)?.groupValues?.get(1)?.let { pieces ->
								last_pieces		= regexPieces_toFloat(pieces)
								last_price_kg	= null
								last_price_pieces = regexPrice_toFloat(price_unit)
								last_weight_kg	= null
								product = null
							}
						}
				}

				// TODO: mode to add weight/pieces of this item to the previous one instead of the next

			}
			// else check if you can add the previously saved data to the current product
			else if( product != null ) {
				if(last_weight_kg != null) {
					// TODO:
				} else if(last_pieces != null) {
					// TODO:
				}

				last_pieces		= null
				last_price_kg	= null
				last_price_pieces = null
				last_weight_kg	= null
			}

			if(product != null && price != null)
				// add if it was not a weight/pieces row, and add default values if a field was not found
				newReceiptItems.add(
					ScanModel(
						product ?: DFLT_ITEM,
						price ?: DFLT_PRICE,
						null,
						price ?: DFLT_PRICE,
						null,
						null,
						null,
						null
					))

			viewModel.receiptItems.value = newReceiptItems

		}
	}


	/* ScanAdapter.IListenerOnClickSelectProduct */

	override fun onClickSelectProduct(position: Int) {
		selectedProductIndex = position
		navController.navigate(
			FragmentScanReceiptDirections.actionFragmentScanReceiptToFragmentProductsWithResult()
		)
	}

	override fun onClickRemove(position: Int) {
		adapter.deleteItem(position)
	}



    companion object {

		private const val LOG_TAG_SCAN = "SCAN"


		// anything (\n shouldn't happen)
		private val REGEX_PRODUCT		= Regex("[^\\n]+")
		// ?: is a non-capturing group; \\D is a non-digit
		private val REGEX_PIECES		= Regex("(\\d ?)+(?:\\D|$)", RegexOption.IGNORE_CASE)
		// admit up to 1 space after each digit, for scanning errors
		private val REGEX_PRICE			= Regex("-?(\\d ?)+[.,] ?(\\d ?){2}")
		private val REGEX_PRICE_ONLY	= Regex("^-?(\\d ?)+[.,] ?(\\d ?){2}$")
		private val REGEX_TAXES_ONLY	= Regex("^(\\d ?){0,3}%")
		private val REGEX_WEIGHT_KG		= Regex("(\\d ?)+[.,] ?(\\d ?){3}")

		private const val DFLT_ITEM		= "[Not found]"
		private const val DFLT_PRICE	= 0.00F

		private val AUTOCOMPLETE_SHOP_BRAND_THRESHOLD = 2

		/**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        @JvmStatic
        fun newInstance() =
            FragmentScanReceipt()
    }
}