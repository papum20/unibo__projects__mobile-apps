package com.papum.homecookscompanion.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A product (either edible or not), belonging to a category.
 * It can be represented by its name,
 * obtained from the concatenation of the names in the parent hierarchy.
 */
@Entity(
	primaryKeys = ["name", "parent"]
)
class Product(
	@PrimaryKey
	var name: String,		// product name
	var parent: String?		// parent name
)
