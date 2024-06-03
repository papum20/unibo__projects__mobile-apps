package com.papum.homecookscompanion.model.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * A product, either edible (food or recipe) or not;
 * a recipe is (probably) made of ingredients (other (edible) products).
 * It can be represented by its name,
 * obtained from the concatenation of the names in the parent hierarchy.
 * Brand can also be last name of hierarchy.
 */
@Entity(
	tableName = "Product",
	indices = [
		Index(value = ["name", "parent"], unique = true)
	]
)
@Parcelize
data class EntityProduct(
	@PrimaryKey(autoGenerate = true)
	var id: Long,

	var name: String,		// product name
	var parent: String,		// parent name

	var isEdible: Boolean,	// edible if it's a food or a recipe
	var isRecipe: Boolean
) : Parcelable {

	override fun toString(): String {
		return "$name <- $parent ($id)"
	}
}
