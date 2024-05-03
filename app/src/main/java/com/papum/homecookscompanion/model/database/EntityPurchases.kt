package com.papum.homecookscompanion.model.database

import androidx.room.Entity
import androidx.room.ForeignKey
import java.time.LocalDateTime

/**
 * A product, either edible (food or recipe) or not;
 * a recipe is (probably) made of ingredients (other (edible) products).
 * It can be represented by its name,
 * obtained from the concatenation of the names in the parent hierarchy.
 * Brand can also be last name of hierarchy.
 */
@Entity(
	tableName = "Prices",
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
	primaryKeys = ["idProduct", "date"]
)
class EntityPurchases(

	var idProduct: Long,
	var idShop: Long,

	var date: LocalDateTime,

	var price: Float,
)

