package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * An abstract edible product.
 * Nutrients are grams for 100g of edible part of product.
 * Null nutrients values will probably be inherited by parent (if available).
 */
@Entity(
	tableName = "Nutrients",
	foreignKeys = [
		ForeignKey(
			entity = EntityProduct::class,
			parentColumns	= ["id"],
			childColumns	= ["idProduct"]
		)
	],
	indices = [
		Index("idProduct")
	]
)
class EntityNutrients(
	@PrimaryKey
	var idProduct: Long,

	var kcal: Float?,
	var carbohydrates: Float?,
	var fats: Float?,
	var proteins: Float?,
)
