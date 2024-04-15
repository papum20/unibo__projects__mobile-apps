package com.papum.homecookscompanion.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * An abstract edible product.
 * Nutrients are grams for 100g of edible part of product.
 * Null nutrients values will probably be inherited by parent (if available).
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
class Edible(
	var name: String,
	var parent: String,

	var kcal: Float?,
	var carbohydrates: Float?,
	var fats: Float?,
	var proteins: Float?,
)
