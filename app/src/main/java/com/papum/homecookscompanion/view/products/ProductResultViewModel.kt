package com.papum.homecookscompanion.view.products

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProduct

class ProductResultViewModel() : ViewModel() {

	var selectedProduct = MutableLiveData<EntityProduct>()


}


class ProductResultViewModelFactory() : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(ProductResultViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return ProductResultViewModel() as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}