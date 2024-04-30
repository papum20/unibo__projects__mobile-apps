package com.papum.homecookscompanion.view.edit.food

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityNutrients
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndNutrients

class EditFoodViewModel(private val repository: Repository) : ViewModel() {


	/* query */

	fun getProduct_fromId(id: Long): LiveData<EntityProductAndNutrients> {
		return repository.getProductWithNutrients_fromId(id)
	}

	/* insert */

	/**
	 * A parent as "" is interpreted as null.
	 */
	fun createFoodWithNutrients(
		name: String,
		parent: String,

		kcal: Float?,
		carbohydrates: Float?,
		fats: Float?,
		proteins: Float?,
	) {
		val newParent =
			if (parent != "") parent
			else null
		return repository.insertProductAndNutrients(
			EntityProduct(
				id = 0,
				name = name,
				parent = newParent,
				isEdible = true,
				isRecipe = false
			),
			EntityNutrients(
				idProduct = 0,
				kcal = kcal,
				carbohydrates = carbohydrates,
				fats = fats,
				proteins = proteins
			)
		)
	}

}


class EditFoodViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(EditFoodViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return EditFoodViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}