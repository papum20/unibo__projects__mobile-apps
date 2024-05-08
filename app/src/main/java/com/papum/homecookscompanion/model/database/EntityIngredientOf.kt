package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * 1-to-many relation between a recipe and its ingredients.
 */
@Entity(
	tableName = "IngredientOf",
	foreignKeys = [
		ForeignKey(
			entity = EntityProduct::class,
			parentColumns	= ["id"],
			childColumns	= ["idRecipe"]
		),
		ForeignKey(
			entity = EntityProduct::class,
			parentColumns	= ["id"],
			childColumns	= ["idIngredient"]
		)
	],
	indices = [
		Index(value = ["idRecipe"]),
		Index(value = ["idIngredient"])
	],
	primaryKeys = ["idRecipe", "idIngredient"]
)
class EntityIngredientOf(
	var idRecipe: Long,
	var idIngredient: Long
)
