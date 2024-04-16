package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * An (abstract) food product.
 */
@Entity(
	tableName = "Food",
	foreignKeys = [
		ForeignKey(
			entity = EntityProduct::class,
			parentColumns	= ["id"],
			childColumns	= ["id"]
		)
	]
)
class EntityFood(
	@PrimaryKey
	var id: Long = 0L,
	
	var brand: String?
)
