package com.papum.homecookscompanion.utils

import android.content.Context
import com.papum.homecookscompanion.R
import com.papum.homecookscompanion.model.database.EntityIngredientOf
import com.papum.homecookscompanion.model.database.EntityNutrients

object UtilProducts {


	fun getRecipeNutrients(
		recipeId: Long,
		ingredients: List<EntityIngredientOf>,
		ingredientsNutrients: List<EntityNutrients>
	): EntityNutrients {

		val weightRecipe = ingredients.sumOf { it.quantityMin.toDouble() }

		return EntityNutrients(recipeId, 0F, 0F, 0F, 0F).apply {
			for (i in ingredients.indices) {
				val nutrients = ingredientsNutrients.find { it.idProduct == ingredients[i].idIngredient }
				if (nutrients == null) {
					continue
				}

				nutrients.kcal?.let {
					kcal = kcal!! + it / Const.MEASURE_QUANTITY * ingredients[i].quantityMin
				}
				nutrients.carbohydrates?.let {
					carbohydrates =
						carbohydrates!! + it / Const.MEASURE_QUANTITY * ingredients[i].quantityMin
				}
				nutrients.fats?.let {
					fats = fats!! + it / Const.MEASURE_QUANTITY * ingredients[i].quantityMin
				}
				nutrients.proteins?.let {
					proteins = proteins!! + it / Const.MEASURE_QUANTITY * ingredients[i].quantityMin
				}
			}
			kcal = (kcal!! / weightRecipe * Const.MEASURE_QUANTITY).toFloat()
			carbohydrates = (carbohydrates!! / weightRecipe * Const.MEASURE_QUANTITY).toFloat()
			fats = (fats!! / weightRecipe * Const.MEASURE_QUANTITY).toFloat()
			proteins = (proteins!! / weightRecipe * Const.MEASURE_QUANTITY).toFloat()
		}

	}


	fun getKcalShort(context: Context, value: Float?): String =
		value?.let {
			context.getString(R.string.nutrients_placeholder_short_kcal, it)
		} ?: context.getString(R.string.quantity_unavailable_kcal)

	fun getCarbShort(context: Context, value: Float?): String =
		value?.let {
			context.getString(R.string.nutrients_placeholder_short_carb, it)
		} ?: context.getString(R.string.quantity_unavailable_carb)

	fun getFatsShort(context: Context, value: Float?): String =
		value?.let {
			context.getString(R.string.nutrients_placeholder_short_fats, it)
		} ?: context.getString(R.string.quantity_unavailable_fats)

	fun getProtShort(context: Context, value: Float?): String =
		value?.let {
			context.getString(R.string.nutrients_placeholder_short_prot, it)
		} ?: context.getString(R.string.quantity_unavailable_prot)


}