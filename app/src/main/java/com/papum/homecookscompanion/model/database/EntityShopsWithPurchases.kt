package com.papum.homecookscompanion.model.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation


@Entity
class EntityShopsWithPurchases (
    @Embedded
    val shop: EntityShops,
    @Relation(
		parentColumn = "id",
		entityColumn = "idShop"
	)
    val purchase: EntityPurchases
 
)

