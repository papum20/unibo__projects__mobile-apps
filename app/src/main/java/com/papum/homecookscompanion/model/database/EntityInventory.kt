package com.papum.homecookscompanion.model.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Table for products in inventory.
 */
@Entity(
	tableName = "Inventory",
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
data class EntityInventory(
	@PrimaryKey
	var idProduct: Long,

	@ColumnInfo(name = "quantity", defaultValue = "0")
	var quantity: Float
)
