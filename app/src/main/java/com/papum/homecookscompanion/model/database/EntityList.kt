package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Table for products in shopping list.
 */
@Entity(
	tableName = "List",
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
class EntityList(
	@PrimaryKey
	var idProduct: Long,
	
	var quantity: Float?
)
