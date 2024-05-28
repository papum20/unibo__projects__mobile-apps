package com.papum.homecookscompanion.view.edit.recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import com.papum.homecookscompanion.model.Repository
import com.papum.homecookscompanion.model.database.EntityIngredientOf
import com.papum.homecookscompanion.model.database.EntityNutrients
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndIngredientOf
import com.papum.homecookscompanion.utils.Const
import com.papum.homecookscompanion.utils.UtilProducts

class EditRecipeViewModel(
	private val repository: Repository,
	private val recipeId: Long
) : ViewModel() {

	private var recipe: EntityProduct? = null

	// ingredients.value can be used with !!, as it's private and managed internally, and never null
	private val ingredients: MutableLiveData<MutableList<EntityProductAndIngredientOf>> =
		MutableLiveData(mutableListOf())
	private var ingredientsInitialized: Boolean = false

	private val displayWeight = MutableLiveData(DFLT_DISPLAY_WEIGHT)


	/* query */

	// ingredients.value can be used with !!, as it's private and managed internally, and never null
	fun fetchIngredients(): LiveData<List<EntityProductAndIngredientOf>> =
		repository.getAllIngredients_fromRecipeId(recipeId)

	fun getRecipe_fromId(id: Long): LiveData<EntityProduct> =
		repository.getProduct(id)

	/**
	 * Get the recipe's nutrients, as sum of ingredient's.
	 */
	fun getRecipeNutrients(): LiveData<EntityNutrients> =
		displayWeight.switchMap { weightDisplay ->

			ingredients.switchMap { ingredients ->
				repository.getNutrients(
					ingredients.map { it.product.id }
				)
					.switchMap { nutrientsList ->
					MutableLiveData(
						UtilProducts.getRecipeNutrients(
							recipeId, ingredients.map { it.ingredientItem }, nutrientsList
						)
					)
		} } }

	fun getRecipeWeight(): LiveData<Double> =
		ingredients.switchMap { ingredients ->
			MutableLiveData(
				ingredients.sumOf { it.ingredientItem.quantityMin.toDouble() }
			)
		}

	/* insert */

	/**
	 * Save recipe, as product with its ingredients.
	 * A parent as "" is interpreted as null.
	 */
	fun saveRecipe(name: String, parent: String) {

		if(ingredients.value == null) {
			Log.wtf(TAG, "Ingredients are null. Cannot save.")
			return
		}

		val newParent =
			if (parent != "") parent
			else null
		val recipe: EntityProduct =
			recipe?.apply {
				this.name = name
				this.parent = newParent
				Log.d(TAG, "fetched id is $id")
			} ?: EntityProduct(
				id = 0,
				name = name,
				parent = newParent,
				isRecipe = true,
				isEdible = true
			)

		repository.insertRecipeAndIngredients(recipe, ingredients.value!!.map{it.ingredientItem})

		Log.d(TAG,
			ingredients.value!!.map {
			it.ingredientItem.apply { idRecipe = recipe.id }
		}.joinToString { "${it.idRecipe} ${it.idIngredient} ${it.quantityMin} ${it.quantityMax}" })
	}

	/* getters/setters */


	/**
	 * Set to a new value, if not initialized.
	 */
	fun initIngredients(newIngredients: MutableList<EntityProductAndIngredientOf>) {
		if(!ingredientsInitialized) {
			ingredients.value = newIngredients
			ingredientsInitialized = true
		}
	}

	fun initRecipe(newRecipe: EntityProduct) {
		if(recipe == null) recipe = newRecipe
	}

	fun getDisplayedWeight(): LiveData<Float> =
		displayWeight

	fun setDisplayedWeight(weight: Float) {
		displayWeight.value = weight
	}

	fun getIngredients(): List<EntityProductAndIngredientOf> =
		ingredients.value!!

	fun addIngredient(product: EntityProduct): EntityProductAndIngredientOf {

		val newIngredient = EntityProductAndIngredientOf(
			product,
			EntityIngredientOf(
				idIngredient = product.id,
				idRecipe = recipeId,
				quantityMax = 0F,
				quantityMin = 0F,
			))
		ingredients.value = ingredients.value!!.apply {
			add(newIngredient)
			Log.d(TAG, "Added ingredient, new size is ${ingredients.value!!.size}")
		}
		return newIngredient
	}

	fun removeIngredient(id: Long) {
		ingredients.value = ingredients.value!!.apply {
			removeIf {  it.product.id == id }
			Log.d(TAG, "Removed ingredient, new size is ${ingredients.value!!.size}")
		}
	}

	fun updateIngredientQuantity(id: Long, quantity: Float): EntityProductAndIngredientOf? {
		var res: EntityProductAndIngredientOf? = null
		ingredients.value = ingredients.value!!.map {
			if (it.product.id == id)
				it.apply {
					ingredientItem.quantityMin = quantity
					res = this
				}
			else it
		}.toMutableList()
		return res
	}


	companion object {

		private const val TAG = "RECIPEvm"

		private const val DFLT_DISPLAY_WEIGHT = Const.MEASURE_QUANTITY

	}

}


class EditRecipeViewModelFactory(
	private val repository: Repository,
	private val recipeId: Long
) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(EditRecipeViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return EditRecipeViewModel(repository, recipeId) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}