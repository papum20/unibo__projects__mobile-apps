package com.papum.homecookscompanion.database

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * A recipe (group of edible products, plus some properties).
 * Still a product.
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
class Recipe(
	var name: String,
	var parent: String,

	var author: String?	// recipe author
)
