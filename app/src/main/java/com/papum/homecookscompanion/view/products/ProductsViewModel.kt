package com.papum.homecookscompanion.view.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProduct

class ProductsViewModel(private val repository: Repository) : ViewModel() {

	fun getAllProducts(): LiveData<List<EntityProduct>> {
		return repository.getAllProducts()
	}

	fun insertProduct(product: EntityProduct) {
		repository.insertProduct(product)
	}

	fun deleteProduct(products: List<EntityProduct>) {
		repository.deleteProducts(products)
	}

}


class ProductsViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(ProductsViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return ProductsViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}