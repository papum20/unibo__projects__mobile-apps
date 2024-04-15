package com.papum.homecookscompanion.database

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * Table for products in inventory.
 */
@Entity(
	tableName = "Inventory",
	foreignKeys = [
		ForeignKey(
			entity = EntityProduct::class,
			parentColumns = ["name", "parent"],
			childColumns = ["name", "parent"]
		)
	],
	primaryKeys = ["name", "parent"]
)
class EntityInventory(
	var name: String,
	var parent: String?,

	var quantity: Float?
)
