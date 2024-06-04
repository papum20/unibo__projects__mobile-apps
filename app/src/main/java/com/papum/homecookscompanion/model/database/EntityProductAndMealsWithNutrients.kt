package com.papum.homecookscompanion.model.database

import androidx.room.Embedded
import androidx.room.Relation

class EntityProductAndMealsWithNutrients(
	@Embedded
	val product: EntityProduct,
	@Relation(
		parentColumn = "id",
		entityColumn = "idEdible"
	)
	val meal: EntityMeals,
	@Relation(
		parentColumn = "id",
		entityColumn = "idProduct"
	)
	val nutrients: EntityNutrients
)