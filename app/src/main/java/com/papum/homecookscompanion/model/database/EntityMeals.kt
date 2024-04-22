package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
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
	primaryKeys = ["date", "idEdible"]
)
class EntityMeals(
	var idEdible: Long,
	var date: LocalDateTime,

	var quantity: Float
)
