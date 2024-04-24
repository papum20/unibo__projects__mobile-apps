package com.papum.homecookscompanion.view.edit.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.papum.homecookscompanion.model.Repository

class EditRecipeViewModel(private val repository: Repository) : ViewModel() {



}


class EditRecipeViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(EditRecipeViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return EditRecipeViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}