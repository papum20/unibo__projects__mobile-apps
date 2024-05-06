package com.papum.homecookscompanion.model.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation


class EntityProductAndInventoryWithAlerts (
    @Embedded
    val product: EntityProduct,
    @Relation(
		parentColumn = "id",
		entityColumn = "idProduct"
	)
    var inventoryItem: EntityInventory?,
	@Relation(
		parentColumn = "id",
		entityColumn = "idProduct"
	)
	var alert: EntityAlerts?
 
)

