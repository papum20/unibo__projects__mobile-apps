package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Table for products in shopping list.
 */
@Entity(
	tableName = "Shops"
)
class EntityShops(
	@PrimaryKey(autoGenerate = true)
	val id: Long,

	val address: String,
	val brand: String,
	val city: String,
	val state: String,
	val latitude: Double,
	val longitude: Double
)
