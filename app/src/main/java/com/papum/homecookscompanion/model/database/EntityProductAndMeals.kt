package com.papum.homecookscompanion.model.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation


@Entity
class EntityProductAndMeals (
    @Embedded
    val product: EntityProduct,
    @Relation(
		parentColumn = "id",
		entityColumn = "idEdible"
	)
    val mealsItem: EntityMeals
 
)

