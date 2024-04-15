package com.papum.homecookscompanion.database

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * An abstract non-edible product.
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
class NonEdible(
	var name: String,
	var parent: String,

	var data: String?	// temporary field, empty table for now
)
