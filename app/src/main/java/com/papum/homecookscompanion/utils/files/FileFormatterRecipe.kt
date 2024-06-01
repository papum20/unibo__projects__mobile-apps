package com.papum.homecookscompanion.utils.files

import android.content.ContentResolver
import android.net.Uri
import com.papum.homecookscompanion.model.database.EntityIngredientOf
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndIngredientOf
import com.papum.homecookscompanion.model.database.EntityRecipeWithIngredients
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter



object FileFormatterRecipe {

	private const val KEY_ID			= "id"
	private const val KEY_NAME			= "name"
	private const val KEY_PARENT		= "parent"
	private const val KEY_IS_EDIBLE		= "is_edible"
	private const val KEY_IS_RECIPE		= "is_recipe"
	private const val KEY_QUANTITY_MAX	= "quantityMax"
	private const val KEY_QUANTITY_MIN	= "quantityMin"
	private const val KEY_INGREDIENTS	= "ingredients"

	const val MIME_TYPE = "application/json"


	fun readRecipe(resolver: ContentResolver, uri: Uri): EntityRecipeWithIngredients? {

		if(uri.path == null) {
			return null
		}
/*
		val fd = resolver.openFileDescriptor(uri, "r")


		val fileInputStream = FileInputStream(fd?.fileDescriptor)

		fileInputStream.bufferedReader()

		val inputStream: InputStream? = resolver.openInputStream(uri)

		inputStream?.use { stream ->

			val r = BufferedReader(InputStreamReader(inputStream))
			val total: StringBuilder = StringBuilder()
			{
				var line: String?
				while ((r.readLine().also { line = it }) != null) {
					total.append(line).append('\n')
				}
			}
			val content = total.toString()

		}

*/



		val file = File(uri.path!!)
		val jsonString = file.readText()
		val jsonData = JSONObject(jsonString)


		try {
			val id			= jsonData.getLong(KEY_ID)
			val name		= jsonData.getString(KEY_NAME)
			val parent		= jsonData.getString(KEY_PARENT)
			val ingredients	= mutableListOf<EntityProductAndIngredientOf>()

			val jsonIngredients = jsonData.getJSONArray(KEY_INGREDIENTS)
			for (i in 0 until jsonIngredients.length()) {

				val ingredientObject = jsonIngredients.getJSONObject(i)

				val ingredientId		= ingredientObject.getLong(KEY_ID)
				val ingredientName		= ingredientObject.getString(KEY_ID)
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

			return EntityRecipeWithIngredients(
				EntityProduct(
					id			= id,
					name		= name,
					parent		= parent,
					isEdible	= true,
					isRecipe	= true
				),
				ingredients
			)

		} catch (_: JSONException) {
			return null
		}


	}

	fun writeRecipe(
		recipe: EntityProduct, ingredients: List<EntityProductAndIngredientOf>, file: File
	) {

		val jsonIngredients = JSONArray()
		ingredients.forEach { ingredient ->
			val ingredientObject = JSONObject().apply {
				put(KEY_ID,				ingredient.product.id)
				put(KEY_NAME,			ingredient.product.name)
				put(KEY_PARENT,			ingredient.product.parent)
				put(KEY_IS_EDIBLE,		ingredient.product.isEdible)
				put(KEY_IS_RECIPE,		ingredient.product.isRecipe)
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