package com.papum.homecookscompanion.database

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * Table for products in inventory.
 */
@Entity(
	foreignKeys = [
		ForeignKey(
			entity = Product::class,
			parentColumns = ["name", "parent"],
			childColumns = ["name", "parent"]
		)
	],
	primaryKeys = ["name", "parent"]
)
class Inventory(
	var name: String,
	var parent: String,

	var quantity: Float?
)
