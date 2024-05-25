package com.papum.homecookscompanion.view.edit.scan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndProductRecognized
import com.papum.homecookscompanion.model.database.EntityProductRecognized
import com.papum.homecookscompanion.model.database.EntityPurchases
import com.papum.homecookscompanion.model.database.EntityShops
import java.time.LocalDateTime

class ScanViewModel(val repository: Repository) : ViewModel() {

	val selectedShop = MutableLiveData<EntityShops>()
	// typed part, to search for suggestions
	val typedShop = MutableLiveData("")

	var receiptItems = MutableLiveData<MutableList<ScanModel>>()


	/**
	 * Using a switchMap, it's always updated with this.receiptItems.
	 */
	fun getRecognizedProducts(): LiveData<List<EntityProductAndProductRecognized>> =
		selectedShop.switchMap { shop ->
			receiptItems.switchMap { items ->
				repository.getAllProductsRecognized_fromTextAndShop(
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

	fun getShops_matchingBrand(): LiveData<List<EntityShops>> =
		typedShop.switchMap { shopSubstr ->
			repository.getAllShops_fromSubstrBrand(shopSubstr)
		}

	/* Insert */

	/**
	 * @throws IllegalArgumentException if no shop is selected
	 */
	fun addAllAssociated_toInventoryAndPurchases() {
		if(selectedShop.value == null)
			throw IllegalArgumentException("No shop selected")

		val dateCurrent = LocalDateTime.now()

		receiptItems.value?.let { items ->
			items.filter { it.productId != null }
				.map { item ->
					EntityInventory(
						item.productId!!,
						item.quantity
					)
				}
				.forEach{ repository.updateInventoryQuantity_sumOrInsert(it) }
			repository.insertManyPurchases(
				items.filter { it.productId != null }
					.map { item ->
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
		repository.insertManyProductsRecognized(
			items.filter { it.productId != null }
				.map { item ->
					EntityProductRecognized(item.recognizedProduct, shopId, item.productId!!)
				}
		)
	}

	/**
	 * @throws IllegalArgumentException if no shop is selected
	 */
	 fun saveAllRecognizedTextAssociations() {
		selectedShop.value?.let { shop ->
			receiptItems.value?.let { items ->
				_saveAllRecognizedTextAssociations(items, shop.id)
			}
		} ?: throw IllegalArgumentException("No shop selected")
	}


	companion object {

		private const val ID_NULL = -1L

	}

}




class ScanViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(ScanViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return ScanViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}