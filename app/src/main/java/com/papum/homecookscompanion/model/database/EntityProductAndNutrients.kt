package com.papum.homecookscompanion.model.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation


@Entity
class EntityProductAndNutrients (
    @Embedded
    val product: EntityProduct,
    @Relation(
		parentColumn = "id",
		entityColumn = "idProduct"
	)
    val listItem: EntityList
 
)

