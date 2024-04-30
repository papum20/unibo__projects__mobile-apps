package com.papum.homecookscompanion.view.meals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProductAndMeals
import java.time.LocalDateTime

class MealsViewModel(private val repository: Repository) : ViewModel() {

	val currentlySetDate: MutableLiveData<LocalDateTime> = MutableLiveData(LocalDateTime.now()) 	// default to today


	fun getAllProducts(): LiveData<List<EntityProductAndMeals>> {
		return repository.getAllProductsWithMeals()
	}

	/**
	 * `month` from 1.
	 */
	fun getAllProducts_fromDate(date: LocalDateTime): LiveData<List<EntityProductAndMeals>> {
		return repository.getAllProductsWithMeals_fromDate(date)
	}

	/**
	 * Get all products for date `this.currentlySetDate`
	 */
	fun getAllProducts_fromDateSet(): LiveData<List<EntityProductAndMeals>> =
		currentlySetDate.switchMap { date ->
			getAllProducts_fromDate(date)
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


class MealsViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(MealsViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return MealsViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}