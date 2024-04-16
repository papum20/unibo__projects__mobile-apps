package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * An abstract non-edible product.
 */
@Entity(
	tableName = "NonEdible",
	foreignKeys = [
		ForeignKey(
			entity = EntityProduct::class,
			parentColumns	= ["id"],
			childColumns	= ["id"]
		)
	]
)
class EntityNonEdible(
	@PrimaryKey
	var id: Long,
	
	var data: String?	// temporary field, empty table for now
)
