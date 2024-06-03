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
import com.papum.homecookscompanion.model.database.EntityRecipeWithIngredientsAndNutrients
import com.papum.homecookscompanion.utils.Const
import com.papum.homecookscompanion.utils.UtilProducts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.jvm.Throws

class EditRecipeViewModel(
	private val repository: Repository,
	private val recipeId: Long
) : ViewModel() {

	private var recipe: EntityProduct? = null

	// ingredients.value can be used with !!, as it's private and managed internally, and never null
	val ingredients: MutableLiveData<MutableList<EntityProductAndIngredientOf>> =
		MutableLiveData(mutableListOf())
	private var ingredientsInitialized: Boolean = false

	var importResult: MutableLiveData<ImportResult?> =
		MutableLiveData(null)

	private val displayWeight = MutableLiveData(DFLT_DISPLAY_WEIGHT)


	/* query */

	// ingredients.value can be used with !!, as it's private and managed internally, and never null
	fun fetchIngredients(): LiveData<List<EntityProductAndIngredientOf>> =
		repository.getAllIngredients_fromRecipeId(recipeId)

	/**
	 * Fetch from repository nutrients for each ingredient.
	 */
	fun fetchNutrients(): List<EntityNutrients> =
		repository.getNutrients_value(
			ingredients.value?.map { it.product.id }
				?: emptyList()
		)

	fun getRecipe_fromId(id: Long): LiveData<EntityProduct?> =
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


	/* import/export */

	/**
	 * Import recipe, adding it and the missing ingredients to the database.
	 * @return ImportResult.DUPLICATE if a product with same name+parent was already
	 * in the database, and had to change its name;
	 * ImportResult.SUCCESS if the recipe was added successfully
	 */
	suspend fun importRecipe(recipe: EntityRecipeWithIngredientsAndNutrients) {

		withContext(Dispatchers.IO) {

			val newRecipe: EntityProduct?
			var result: ImportResult? = ImportResult.SUCCESS

			// check if a product with same name+parent already exists
			val localRecipe =
				repository.getProductFromName_value(recipe.recipe.name, recipe.recipe.parent)
			if (localRecipe != null) {
				var counter = 1
				while(
					repository.getProductFromName_value("${recipe.recipe.name} ($counter)", recipe.recipe.parent)
					!= null
				) counter++
				newRecipe = localRecipe.apply {
					id = 0
					name = "$name ($counter)"
				}
				result = ImportResult.DUPLICATE
			} else
				newRecipe = recipe.recipe.apply {
					id = 0
				}

			// then we have to create its ingredients (if not already present)
			val newIngredients = mutableListOf<EntityProductAndIngredientOf>()
			for (ingredient in recipe.ingredients) {
				val localIngredient =
					repository.getProductFromName_value(
						ingredient.product.name,
						ingredient.product.parent
					)

				if (localIngredient == null) {
					val newIngredientProduct = EntityProduct(
						id = 0,
						name = ingredient.product.name,
						parent = ingredient.product.parent,
						// an ingredient should be edible
						isEdible = true,
						// TODO: recursive recipes aren't supported yet,
						//  so we consider an imported ingredient as a recipe
						isRecipe = false
					)
					val newId = repository.insertProduct_result(newIngredientProduct)
					Log.d(TAG, "new ingredient '${ingredient.product.name}' id is $newId")
					newIngredients.add(
						EntityProductAndIngredientOf(
							newIngredientProduct.apply {
								id = newId
							},
							ingredient.ingredientItem.apply {
								idIngredient = newId
							}
						)
					)
				} else {
					newIngredients.add(
						EntityProductAndIngredientOf(
							localIngredient,
							ingredient.ingredientItem.apply {
								idIngredient = localIngredient.id
							}
						)
					)
				}
			}

			// finally, add the recipe
			this@EditRecipeViewModel.recipe = newRecipe
			this@EditRecipeViewModel.ingredients.postValue(newIngredients)

			Log.d(TAG, "new ingredients value ${newIngredients.joinToString { "${it.ingredientItem.idRecipe}-${it.ingredientItem.idIngredient}" }}")

			importResult.postValue(result)

			Log.d(TAG, "ViewModel imported recipe, with id ${recipe.recipe.id}")
		}
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

		val recipe: EntityProduct =
			recipe?.apply {
				this.name	= name
				this.parent	= parent
				Log.d(TAG, "class's recipe id is $id")
			} ?: EntityProduct(
				id = 0,
				name = name,
				parent = parent,
				isRecipe = true,
				isEdible = true
			)

		repository.insertRecipeAndIngredients(recipe, ingredients.value!!.map{it.ingredientItem})

		Log.d(TAG,
			ingredients.value!!.map {
			it.ingredientItem.apply { idRecipe = recipe.id }
		}.joinToString { "${it.idRecipe}-${it.idIngredient}" })
	}

	/* getters/setters */


	/**
	 * Set to a new value, if not initialized.
	 * @return true if the value was set, false if it had already been initialized
	 */
	fun initIngredients(newIngredients: MutableList<EntityProductAndIngredientOf>): Boolean {
		if(!ingredientsInitialized) {
			ingredients.value = newIngredients
			ingredientsInitialized = true
			return true
		}
		return false
	}

	fun initRecipe(newRecipe: EntityProduct) {
		if(recipe == null) recipe = newRecipe
	}

	fun getDisplayedWeight(): LiveData<Float> =
		displayWeight

	fun getIngredients(): List<EntityProductAndIngredientOf> =
		ingredients.value!!

	fun getRecipe(): EntityProduct? =
		recipe

	fun setDisplayedWeight(weight: Float) {
		displayWeight.value = weight
	}

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


		enum class ImportResult {
			DUPLICATE,
			SUCCESS
		}

	}

}


class EditRecipeViewModelFactory(
	private val repository: Repository,
	private val recipeId: Long
) : ViewModelProvider.Factory {

	@Throws(IllegalArgumentException::class)
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if(modelClass.isAssignableFrom(EditRecipeViewModel::class.java)) {
			//@Suppress("UNCHECKED_CAST")
			return EditRecipeViewModel(repository, recipeId) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}