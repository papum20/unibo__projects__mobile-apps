package com.papum.homecookscompanion.view.edit.scan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import com.google.mlkit.vision.text.Text
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndProductRecognized
import com.papum.homecookscompanion.model.database.EntityProductRecognized
import com.papum.homecookscompanion.model.database.EntityPurchases
import com.papum.homecookscompanion.model.database.EntityShops
import java.time.LocalDateTime
import kotlin.jvm.Throws

class ScanViewModel(val repository: Repository) : ViewModel() {

	val selectedShop = MutableLiveData<EntityShops>()
	// typed part, to search for suggestions
	val typedShop = MutableLiveData("")

	val receiptItems = MutableLiveData<MutableList<ScanModel>>()


	/**
	 * Using a switchMap, it's always updated with this.receiptItems.
	 */
	fun getRecognizedProducts(): LiveData<List<EntityProductAndProductRecognized>> =
		selectedShop.switchMap { shop ->
			receiptItems.switchMap { items ->
				repository.getMatchingProductsRecognized(
					items.map { it.recognizedProduct.trim() },
					shop?.id ?: ID_NULL
				)
			}
		}

	fun setAllAssociations(products_withRecognizedAssociations: List<EntityProductAndProductRecognized>) {

		receiptItems.value?.let { items ->

			products_withRecognizedAssociations.forEach { product_withRecognizedAssociations ->
				product_withRecognizedAssociations.recognized.forEach { recognized ->
					// if scan item has not an association yet, search one in the data from db
					items.find {
						it.recognizedProduct == recognized.recognizedText
					}?.let { item ->
						setAssociation(item, product_withRecognizedAssociations.product)
					}
				}
			}

		}
	}

	fun setAssociation(item: ScanModel, product: EntityProduct) {
		item.product	= product.toString()
		item.productId	= product.id
	}


	/* Query */

	fun getMatchingShops(): LiveData<List<EntityShops>> =
		typedShop.switchMap { shopSubstr ->
			repository.getMatchingShops(shopSubstr)
		}

	/* Insert */

	/**
	 * @throws IllegalArgumentException if no shop is selected
	 */
	@Throws(IllegalArgumentException::class)
	fun addAllAssociated_toInventoryAndPurchases() {
		if(selectedShop.value == null)
			throw IllegalArgumentException("No shop selected")

		val dateCurrent = LocalDateTime.now()

		receiptItems.value?.filter { it.productId != null }
			?.let { items ->
				items.map { item ->
					EntityInventory(
						item.productId!!,
						item.quantity ?: DFLT_QUANTITY,
					)
				}
				.forEach{
					Log.d(TAG, "Adding to inventory: ${it.idProduct}, ${it.quantity}")
					repository.addInventoryItemQuantity(it)
				}
				repository.insertPurchases(
					items.map { item ->
						EntityPurchases(
							0,
							item.productId!!,
							selectedShop.value!!.id,
							dateCurrent,
							item.price
						)
					}
				)
			}
	}

	private fun _saveAllRecognizedTextAssociations(items: List<ScanModel>, shopId: Long) {
		repository.insertProductsRecognized(
			items.filter { it.productId != null }
				.map { item ->
					EntityProductRecognized(item.recognizedProduct, shopId, item.productId!!)
				}
		)
	}

	/**
	 * @throws IllegalArgumentException if no shop is selected
	 */
	@Throws(IllegalArgumentException::class)
	 fun saveAllRecognizedTextAssociations() {
		selectedShop.value?.let { shop ->
			receiptItems.value?.let { items ->
				_saveAllRecognizedTextAssociations(items, shop.id)
			}
		} ?: throw IllegalArgumentException("No shop selected")
	}


	/* OCR (image processing) */

	/**
	 * Parse the recognized text, coupling the i-th found product with the i-th found price.
	 */
	fun processImage(visionText: Text) {

		Log.d(TAG, visionText.text)

		val recognizedLines = mutableListOf<Text.Line>()
		for (block in visionText.textBlocks)
			for (line in block.lines)
				recognizedLines.add(line)

		val lines = groupBlocksInLines(recognizedLines)
		updateReceiptItems(lines)

		Log.d(TAG, lines.joinToString("\n") {
			it.joinToString("\t") {
				//"ITEM: ${it.boundingBox}, ${it.boundingBox!!.centerY()}, ${it.text}"
				"{${it.text}}"
			}
		})

	}


	/** Group lines with same Y, and sort by X.
	 */
	private fun groupBlocksInLines(blocks: List<Text.Line>): MutableList<List<Text.Line>> {

		// sort by height
		val blocks_sorted = blocks.sortedBy { it.boundingBox?.centerY() }
		//Log.i(LOG_TAG_SCAN + "2", recognizedLines_sorted.joinToString("\n") { it.text })

		val lines = mutableListOf<List<Text.Line>>()
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
	private fun areBlocksAtSameY(blocksWithTexts: List<Text.Line>): Boolean {

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
	private fun updateReceiptItems(lines: List<List<Text.Line>>) {

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

			receiptItems.value = newReceiptItems

		}
	}


	companion object {

		private const val TAG = "SCANvm"

		private const val ID_NULL = -1L

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
		private const val DFLT_QUANTITY	= 0.00F

	}

}




class ScanViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	@Throws(IllegalArgumentException::class)
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(ScanViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return ScanViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}