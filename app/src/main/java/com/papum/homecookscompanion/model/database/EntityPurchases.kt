package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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
		Index(value = ["idProduct", "date"], unique = true),
		Index(value = ["idShop"])
  	],
	primaryKeys = ["idProduct", "date"]
)
class EntityPurchases(

	var idProduct: Long,
	var idShop: Long,

	var date: LocalDateTime,

	var price: Float?,
)

