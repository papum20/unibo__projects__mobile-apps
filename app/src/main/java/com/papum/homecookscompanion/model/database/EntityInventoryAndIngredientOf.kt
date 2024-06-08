package com.papum.homecookscompanion.model.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation


@Entity
class EntityInventoryAndIngredientOf (
    @Embedded
    val inventoryItem: EntityInventory,
    @Relation(
		parentColumn = "idProduct",
		entityColumn = "idIngredient"
	)
    var ingredientItem: EntityIngredientOf
 
)

