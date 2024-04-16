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
			entity = EntityEdible::class,
			parentColumns	= ["id"],
			childColumns	= ["idEdible"]
		),
		ForeignKey(
			entity = EntityRecipe::class,
			parentColumns	= ["id"],
			childColumns	= ["idRecipe"]
		)
	],
	primaryKeys = ["idEdible", "idRecipe"]
)
class EntityIngredient(
	var idEdible: Long,
	var idRecipe: Long
)
