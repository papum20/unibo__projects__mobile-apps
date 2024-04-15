package com.papum.homecookscompanion.database

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * An (abstract) food product.
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
class Food(
	var name: String,
	var parent: String,

	var brand: String?
)
