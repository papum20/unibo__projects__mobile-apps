package com.papum.homecookscompanion.utils

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


}