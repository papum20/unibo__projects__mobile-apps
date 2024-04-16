package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Table for products in inventory.
 */
@Entity(
	tableName = "Inventory",
	foreignKeys = [
		ForeignKey(
			entity = EntityProduct::class,
			parentColumns	= ["id"],
			childColumns	= ["idProduct"]
		)
	]
)
class EntityInventory(
	@PrimaryKey
	var idProduct: Long,
	
	var quantity: Float?
)
