package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * 1-to-many relation between a recipe and its ingredients.
 */
@Entity(
	tableName = "Ingredient",
	foreignKeys = [
		ForeignKey(
			entity = EntityProduct::class,
			parentColumns = ["name", "parent"],
			childColumns = ["edibleName", "edibleName"]
		),
		ForeignKey(
			entity = EntityRecipe::class,
			parentColumns = ["name", "parent"],
			childColumns = ["recipeName", "recipeParent"]
		)
	],
	primaryKeys = ["edibleName", "edibleParent", "recipeName", "recipeParent"]
)
class EntityIngredient(
	var edibleName: String,
	var edibleParent: String?,

	var recipeName: String,
	var recipeParent: String?,
)
