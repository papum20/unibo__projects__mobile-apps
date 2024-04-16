package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A product (either edible or not), belonging to a category.
 * It can be represented by its name,
 * obtained from the concatenation of the names in the parent hierarchy.
 */
@Entity(
	tableName = "Product"
)
class EntityProduct(
	@PrimaryKey(autoGenerate = true)
	var id: Long,

	var name: String,		// product name
	var parent: String?		// parent name
)
