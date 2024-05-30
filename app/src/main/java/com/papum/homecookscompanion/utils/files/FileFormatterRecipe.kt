package com.papum.homecookscompanion.utils.files

import android.net.Uri
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndIngredientOf
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter



object FileFormatterRecipe {

	private const val KEY_ID			= "id"
	private const val KEY_NAME			= "name"
	private const val KEY_PARENT		= "parent"
	private const val KEY_QUANTITY_MAX	= "quantityMax"
	private const val KEY_QUANTITY_MIN	= "quantityMin"
	private const val KEY_INGREDIENTS	= "ingredients"

	const val MIME_TYPE = "application/json"


	fun writeRecipe(
		recipe: EntityProduct, ingredients: List<EntityProductAndIngredientOf>, file: File
	) {

		val jsonIngredients = JSONArray()
		ingredients.forEach { ingredient ->
			val ingredientObject = JSONObject().apply {
				put(KEY_ID,				ingredient.product.id)
				put(KEY_NAME,			ingredient.product.name)
				put(KEY_PARENT,			ingredient.product.parent)
				put(KEY_QUANTITY_MAX,	ingredient.ingredientItem.quantityMax)
				put(KEY_QUANTITY_MIN,	ingredient.ingredientItem.quantityMin)
			}
			jsonIngredients.put(ingredientObject)
		}


		val jsonData = JSONObject().apply {
			put(KEY_ID,				recipe.id)
			put(KEY_PARENT,			recipe.parent)
			put(KEY_NAME,			recipe.name)
			put(KEY_INGREDIENTS,	jsonIngredients)
		}

		val fileOutputStream = FileOutputStream(file)
		val outputWriter = OutputStreamWriter(fileOutputStream)
		outputWriter.write(jsonData.toString())
		outputWriter.close()
	}


}