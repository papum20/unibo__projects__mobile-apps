package com.papum.homecookscompanion.view.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityList
import com.papum.homecookscompanion.model.database.EntityMeals
import com.papum.homecookscompanion.model.database.EntityProduct
import java.time.LocalDateTime
import kotlin.jvm.Throws

class ProductsViewModel(private val repository: Repository) : ViewModel() {


	/* Query */

	fun getAllProducts(): LiveData<List<EntityProduct>> {
		return repository.getAllProducts()
	}

	fun getProduct_fromId(id: Long): LiveData<EntityProduct?> {
		return repository.getProduct(id)
	}

	fun getAllProducts_fromSubstr_caseInsensitive(substr: String): LiveData<List<EntityProduct>> {
		return repository.getAllProducts_fromSubstr_caseInsensitive(substr)
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


	/*
	fun insertProduct(product: EntityProduct) {
		repository.insertProduct(product)
	}

	fun deleteProduct(products: List<EntityProduct>) {
		repository.deleteProducts(products)
	}
	 */

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