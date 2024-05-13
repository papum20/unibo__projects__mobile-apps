package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
	tableName = "Shops",
	indices = [
		Index(value = ["address", "brand", "city", "state"], unique = true)
	],
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
) {

	override fun toString(): String {
		return "$brand, $address, $city, $state"
	}
}
