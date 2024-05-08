package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * An (abstract) food product.
 */
@Entity(
	tableName = "Meals",
	foreignKeys = [
		ForeignKey(
			entity = EntityProduct::class,
			parentColumns	= ["id"],
			childColumns	= ["idEdible"]
		)
	],
	indices = [
		Index("idEdible")
	]
)
class EntityMeals(
	@PrimaryKey(autoGenerate = true)
	var idMeal: Long,

	var idEdible: Long,
	var date: LocalDateTime,

	var quantity: Float
)
