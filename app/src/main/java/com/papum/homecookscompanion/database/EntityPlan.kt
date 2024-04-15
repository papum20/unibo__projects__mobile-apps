package com.papum.homecookscompanion.database

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
			parentColumns = ["name", "parent"],
			childColumns = ["edibleName", "edibleParent"]
		)
	],
	primaryKeys = ["date", "edibleName", "edibleParent"]
)
class EntityPlan(
	var date: Date,

	var edibleName: String,
	var edibleParent: String?
)
