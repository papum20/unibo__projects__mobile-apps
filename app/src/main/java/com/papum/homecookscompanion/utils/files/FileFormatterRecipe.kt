package com.papum.homecookscompanion.utils.files

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import com.papum.homecookscompanion.model.database.EntityIngredientOf
import com.papum.homecookscompanion.model.database.EntityNutrients
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndIngredientOf
import com.papum.homecookscompanion.model.database.EntityRecipeWithIngredientsAndNutrients
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.time.LocalDateTime
import java.time.ZoneOffset


object FileFormatterRecipe {

	const val MIME_TYPE = "application/json"


	private const val TAG = "FILE_FORMATTER"

	// JSON keys
	private const val KEY_INGREDIENTS	= "ingredients"
	private const val KEY_NUTRIENTS		= "nutrients"

	private const val KEY_ID			= "id"
	private const val KEY_NAME			= "name"
	private const val KEY_PARENT		= "parent"
	private const val KEY_IS_EDIBLE		= "is_edible"
	private const val KEY_IS_RECIPE		= "is_recipe"

	private const val KEY_QUANTITY_MAX	= "quantityMax"
	private const val KEY_QUANTITY_MIN	= "quantityMin"

	private const val KEY_KCAL			= "kcal"
	private const val KEY_CARBOHYDRATES	= "carbohydrates"
	private const val KEY_FATS			= "fats"
	private const val KEY_PROTEINS		= "proteins"

	// Formatting
	private const val NULL_STRING	= ""



	fun readRecipe(resolver: ContentResolver, uri: Uri): EntityRecipeWithIngredientsAndNutrients? {

		if(uri.path == null) {
			return null
		}

		try {
			val fileContent	= UtilFiles.readTextFromUri(resolver, uri)
			Log.d(TAG, "read content: $fileContent")

			val jsonData	= JSONObject(fileContent)

			val id			= jsonData.getLong(KEY_ID)
			val name		= jsonData.getString(KEY_NAME)
			val parent		= jsonData.getString(KEY_PARENT)
			val ingredients	= mutableListOf<EntityProductAndIngredientOf>()
			val nutrients	= mutableListOf<EntityNutrients>()

			with(jsonData.getJSONArray(KEY_INGREDIENTS)) {
				for (i in 0 until length()) {

					val ingredientObject = getJSONObject(i)

					val ingredientId		= ingredientObject.getLong(KEY_ID)
					val ingredientName		= ingredientObject.getString(KEY_NAME)
					val ingredientParent	= ingredientObject.getString(KEY_PARENT)
					val ingredientIsEdible	= ingredientObject.getBoolean(KEY_IS_EDIBLE)
					val ingredientIsRecipe	= ingredientObject.getBoolean(KEY_IS_RECIPE)
					val quantityMax			= ingredientObject.getDouble(KEY_QUANTITY_MAX).toFloat()
					val quantityMin			= ingredientObject.getDouble(KEY_QUANTITY_MIN).toFloat()

					ingredients.add(
						EntityProductAndIngredientOf(
							EntityProduct(
								id			= ingredientId,
								name		= ingredientName,
								parent		= ingredientParent,
								isEdible	= ingredientIsEdible,
								isRecipe	= ingredientIsRecipe
							),
						EntityIngredientOf(
							idIngredient	= ingredientId,
							idRecipe		= id,
							quantityMax		= quantityMax,
							quantityMin		= quantityMin
						)
					))
				}
			}

			with(jsonData.getJSONArray(KEY_NUTRIENTS)) {
				for (i in 0 until length()) {

					val nutrientsObject = getJSONObject(i)

					val nutrientsId				= nutrientsObject.getLong(KEY_ID)
					val nutrientsKcal			= JSONtoFloatOrNull( nutrientsObject.getString(KEY_KCAL) )
					val nutrientsCarbohydrates	= JSONtoFloatOrNull( nutrientsObject.getString(KEY_CARBOHYDRATES) )
					val nutrientsFats			= JSONtoFloatOrNull( nutrientsObject.getString(KEY_FATS) )
					val nutrientsProteins		= JSONtoFloatOrNull( nutrientsObject.getString(KEY_PROTEINS) )

					nutrients.add(
						EntityNutrients(
							idProduct		= nutrientsId,
							kcal			= nutrientsKcal,
							carbohydrates	= nutrientsCarbohydrates,
							fats			= nutrientsFats,
							proteins		= nutrientsProteins
						)
					)
				}
			}

			return EntityRecipeWithIngredientsAndNutrients(
				EntityProduct(
					id			= id,
					name		= name,
					parent		= parent,
					isEdible	= true,
					isRecipe	= true
				),
				ingredients,
				nutrients
			)

		} catch (e: JSONException) {
			Log.e(TAG, "JSONException $e")
			e.printStackTrace()
			return null
		} catch(e: IOException) {
			Log.e(TAG, "IOException $e")
			e.printStackTrace()
			return null
		}


	}

	fun writeRecipe(
		file: File,
		recipe: EntityRecipeWithIngredientsAndNutrients
	) {

		val jsonIngredients = JSONArray()
		recipe.ingredients.forEach { ingredient ->
			val nutrientsObject = JSONObject().apply {
				put(KEY_ID,				ingredient.product.id)
				put(KEY_NAME,			ingredient.product.name)
				put(KEY_PARENT,			ingredient.product.parent)
				put(KEY_IS_EDIBLE,		ingredient.product.isEdible)
				put(KEY_IS_RECIPE,		ingredient.product.isRecipe)
				put(KEY_QUANTITY_MAX,	ingredient.ingredientItem.quantityMax)
				put(KEY_QUANTITY_MIN,	ingredient.ingredientItem.quantityMin)
			}
			jsonIngredients.put(nutrientsObject)
		}

		val jsonNutrients = JSONArray()
		recipe.ingredientsNutrients.forEach { nutrients ->
			val ingredientObject = JSONObject().apply {
				put(KEY_ID,				nutrients.idProduct)
				// be careful with Float? , use empty string as null
				put(KEY_KCAL,			floatOrNullToJSON( nutrients.kcal ))
				put(KEY_CARBOHYDRATES,	floatOrNullToJSON( nutrients.carbohydrates ))
				put(KEY_FATS,			floatOrNullToJSON( nutrients.fats ))
				put(KEY_PROTEINS,		floatOrNullToJSON( nutrients.proteins ))
			}
			jsonNutrients.put(ingredientObject)
		}

		val jsonData = JSONObject().apply {
			put(KEY_ID,				recipe.recipe.id)
			put(KEY_PARENT,			recipe.recipe.parent)
			put(KEY_NAME,			recipe.recipe.name)
			put(KEY_INGREDIENTS,	jsonIngredients)
			put(KEY_NUTRIENTS,		jsonNutrients)
		}

		val fileOutputStream = FileOutputStream(file)
		val outputWriter = OutputStreamWriter(fileOutputStream)
		outputWriter.write(jsonData.toString())
		outputWriter.close()
	}


	/**
	 * Generate a file name for the recipe to export.
	 */
	fun getFileName(recipe: EntityProduct): String =
		"recipe_${LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)}_${recipe.id}-${recipe.name}.json"


	private fun JSONtoFloatOrNull(value: String): Float? =
		if (value == NULL_STRING) {
			null
		} else {
			value.toFloat()
		}

	private fun floatOrNullToJSON(value: Float?): String =
		value?.toString() ?: NULL_STRING

}