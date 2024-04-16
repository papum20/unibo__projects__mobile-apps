package com.papum.homecookscompanion.model.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
class EntityProductAndFood (
    @Embedded
    val product: EntityProduct,
    @Relation(
		parentColumn = "id",
		entityColumn = "id"
	)
    val food: EntityFood
 
)
