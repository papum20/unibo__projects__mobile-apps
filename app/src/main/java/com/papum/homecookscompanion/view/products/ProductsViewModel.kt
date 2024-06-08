package com.papum.homecookscompanion.view.products

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
import java.time.LocalDateTime
import kotlin.jvm.Throws

class ProductsViewModel(private val repository: Repository) : ViewModel() {

	private val substr = MutableLiveData("")

	val filter = MutableLiveData(ARG_FILTER_DFLT)


	/* Query */

	fun getProducts(): LiveData<List<EntityProduct>> =
		filter.switchMap { filter ->
			if(filter == ARG_FILTER_RECIPES)
				substr.switchMap { substr ->
					repository.getMatchingProductsRecipes_caseInsensitive(substr)
				}
			else
				substr.switchMap { substr ->
					repository.getMatchingProducts_caseInsensitive(substr)
				}
		}

	fun getProduct(id: Long): LiveData<EntityProduct?> {
		return repository.getProduct(id)
	}

	/* Insert */

	fun addToInventory(id: Long, quantity: Float) {
		repository.addInventoryItemQuantity(EntityInventory(id, quantity))
	}

	fun addToList(id: Long, quantity: Float) {
		repository.addListItemQuantity(EntityList(id, quantity))
	}

	fun addToMeals(id: Long, date: LocalDateTime, quantity: Float) {
		repository.addMeal(EntityMeals(0, id, date, quantity))
	}


	/* getters/setters */

	fun setSubstr(substr: String) {
		this.substr.value = substr
	}


	companion object {

		const val ARG_FILTER_ALL		= 0
		const val ARG_FILTER_RECIPES	= 1
		const val ARG_FILTER_DFLT		= ARG_FILTER_ALL

	}


}


class ProductsViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	@Throws(IllegalArgumentException::class)
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(ProductsViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return ProductsViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}