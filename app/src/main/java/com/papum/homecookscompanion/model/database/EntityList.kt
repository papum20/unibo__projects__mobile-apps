package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * Table for products in shopping list.
 */
@Entity(
	tableName = "List",
	foreignKeys = [
		ForeignKey(
			entity = EntityProduct::class,
			parentColumns = ["name", "parent"],
			childColumns = ["name", "parent"]
		)
	],
	primaryKeys = ["name", "parent"]
)
class EntityList(
	var name: String,
	var parent: String?,

	var quantity: Float?
)
