package com.papum.homecookscompanion.view.edit.scan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityList
import com.papum.homecookscompanion.model.database.EntityMeals
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndInventory
import com.papum.homecookscompanion.model.database.EntityProductAndInventoryWithAlerts
import com.papum.homecookscompanion.model.database.EntityProductAndList
import com.papum.homecookscompanion.model.database.EntityProductAndMeals
import com.papum.homecookscompanion.model.database.EntityProductAndProductRecognized
import com.papum.homecookscompanion.model.database.EntityProductRecognized
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
			Log.d("SHOP", shop?.toString() ?: "null")
			receiptItems.switchMap { items ->
				Log.d("ITEMS", items.joinToString { it.recognizedProduct })
				Log.d("ITEMS", items.joinToString { it.recognizedProduct.trim() })
				repository.getAllProductsRecognized_fromTextAndShop(
					items.map { it.recognizedProduct.trim() },
					shop.id
				)
			}
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
	fun addRecognizedTextAssociation(recognizedText: String, productId: Long) {
		selectedShop.value?.let { shop ->
			repository.insertProductRecognized(
				EntityProductRecognized(recognizedText, shop.id, productId)
			)
		} ?: throw IllegalArgumentException("No shop selected")
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