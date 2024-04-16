package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * A recipe (group of edible products, plus some properties).
 * Still a product.
 */
@Entity(
	tableName = "Recipe",
	foreignKeys = [
		ForeignKey(
			entity = EntityProduct::class,
			parentColumns	= ["id"],
			childColumns	= ["id"]
		)
	]
)
class EntityRecipe(
	@PrimaryKey
	var id: Long,

	var author: String?	// recipe author
)
