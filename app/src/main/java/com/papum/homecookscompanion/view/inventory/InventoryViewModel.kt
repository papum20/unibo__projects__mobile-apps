package com.papum.homecookscompanion.view.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProductAndInventory
import com.papum.homecookscompanion.model.database.EntityProductAndInventoryWithAlerts

class InventoryViewModel(private val repository: Repository) : ViewModel() {

	fun getAllProductsInInventory(): LiveData<List<EntityProductAndInventory>> {
		return repository.getAllProductsWithInventory()
	}

	fun getAllProductsInInventoryWithAlerts(): LiveData<List<EntityProductAndInventoryWithAlerts>> {
		return repository.getAllProductsWithInventoryAndAlerts()
	}

	/*
	fun insertProduct(product: EntityProduct) {
		repository.insertProduct(product)
	}

	fun deleteProduct(products: List<EntityProduct>) {
		repository.deleteProducts(products)
	}
	 */

}


class InventoryViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return InventoryViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}