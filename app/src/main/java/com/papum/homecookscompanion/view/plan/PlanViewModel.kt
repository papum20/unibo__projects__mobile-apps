package com.papum.homecookscompanion.view.plan

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProductAndList
import com.papum.homecookscompanion.model.database.EntityProductAndPlan

class PlanViewModel(private val repository: Repository) : ViewModel() {

	fun getAllProducts(): LiveData<List<EntityProductAndPlan>> {
		return repository.getAllProductsWithPlan()
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


class PlanViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(PlanViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return PlanViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}