package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.Date

/**
 * An (abstract) food product.
 */
@Entity(
	tableName = "Plan",
	foreignKeys = [
		ForeignKey(
			entity = EntityProduct::class,
			parentColumns	= ["id"],
			childColumns	= ["idEdible"]
		)
	],
	primaryKeys = ["date", "idEdible"]
)
class EntityPlan(
	var idEdible: Long,
	var date: Date,

	var quantity: Float
)
