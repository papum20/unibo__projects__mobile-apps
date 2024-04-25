package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Each entry contains a product and the alert as min quantity to have in inventory.
 */
@Entity(
	tableName = "Alerts",
	foreignKeys = [
		ForeignKey(
			entity = EntityProduct::class,
			parentColumns	= ["id"],
			childColumns	= ["idProduct"]
		)
	]
)
class EntityAlerts(
	@PrimaryKey
	var idProduct: Long,
	
	var quantity: Float
)
