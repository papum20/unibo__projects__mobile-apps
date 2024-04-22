package com.papum.homecookscompanion.view.create.food

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityProductAndList

class CreateFoodViewModel(private val repository: Repository) : ViewModel() {



}


class CreateFoodViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(CreateFoodViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return CreateFoodViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}