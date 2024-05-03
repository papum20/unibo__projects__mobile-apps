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
	var id: Long,
	
	var brand: String
)
