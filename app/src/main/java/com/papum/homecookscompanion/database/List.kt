package com.papum.homecookscompanion.database

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * Table for products in shopping list.
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
class List(
	var name: String,
	var parent: String,

	var quantity: Float?
)
