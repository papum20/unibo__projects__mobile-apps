package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.LocalDateTime

/**
 * An association between a text recognized from a scan, for a shop, with a product (by its id).
 * many-to-1: a recognized text, for a shop, can be associated with only one product.
 */
@Entity(
	tableName = "ProductRecognized",
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
		Index(value = ["recognizedText", "idShop"], unique = true),
		Index(value = ["idProduct"])
  	],
	primaryKeys = ["recognizedText", "idShop"]
)
class EntityProductRecognized(

	val recognizedText: String,
	val idShop: Long,

	val idProduct: Long
)

