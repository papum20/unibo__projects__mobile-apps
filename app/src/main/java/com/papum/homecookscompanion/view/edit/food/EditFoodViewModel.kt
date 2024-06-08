package com.papum.homecookscompanion.view.edit.food

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityNutrients
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndNutrients
import com.papum.homecookscompanion.utils.Const
import kotlin.jvm.Throws

class EditFoodViewModel(private val repository: Repository) : ViewModel() {


	/* query */

	fun getDisplayWeight(): Float =
		DFLT_DISPLAY_WEIGHT

	fun getProduct_fromId(id: Long): LiveData<EntityProductAndNutrients> =
		repository.getProductEdible(id)

	/* insert */

	/**
	 * Save food and its nutrients.
	 * A parent as "" is interpreted as null.
	 */
	fun createFood(
		name: String,
		parent: String,

		kcal: Float?,
		carbohydrates: Float?,
		fats: Float?,
		proteins: Float?,
	) {
		repository.insertProductEdible(
			EntityProduct(
				id = 0,
				name = name,
				parent = parent,
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
	fun createProductNonEdible(
		name: String,
		parent: String,
	) {
		repository.insertProductNonEdible(
			EntityProduct(
				id = 0,
				name = name,
				parent = parent,
				isEdible = false,
				isRecipe = false
			)
		)
	}


	companion object {

		private const val DFLT_DISPLAY_WEIGHT = Const.MEASURE_QUANTITY

	}


}


class EditFoodViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

	@Throws(IllegalArgumentException::class)
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(EditFoodViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return EditFoodViewModel(repository) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}