package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity(
	tableName = "Purchases",
	foreignKeys = [
		ForeignKey(
			entity = EntityShops::class,
			parentColumns	= ["id"],
			childColumns	= ["idShop"]
		),
		ForeignKey(
			entity = EntityProduct::class,
			parentColumns	= ["id"],
			childColumns	= ["idProduct"]
		)
	],
	indices = [
		Index(value = ["idProduct"]),
		Index(value = ["idShop"])
  	]
)
class EntityPurchases(

	@PrimaryKey(autoGenerate = true)
	val id: Long,

	var idProduct: Long,
	var idShop: Long,

	var date: LocalDateTime,

	var price: Float?,
)

