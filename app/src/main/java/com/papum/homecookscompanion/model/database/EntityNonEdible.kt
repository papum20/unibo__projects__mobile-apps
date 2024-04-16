package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * An abstract non-edible product.
 */
@Entity(
	tableName = "NonEdible",
	foreignKeys = [
		ForeignKey(
			entity = EntityProduct::class,
			parentColumns = ["name", "parent"],
			childColumns = ["name", "parent"]
		)
	],
	primaryKeys = ["name", "parent"]
)
class EntityNonEdible(
	var name: String,
	var parent: String?,

	var data: String?	// temporary field, empty table for now
)
