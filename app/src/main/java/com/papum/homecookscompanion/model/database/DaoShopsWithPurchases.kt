package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DaoShopsWithPurchases {

	/* query */

	@Query("""
        SELECT *
        FROM Shops
        INNER JOIN Purchases ON Shops.id = Purchases.idShop
		WHERE idProduct = :productId
    """)
	@Transaction
	fun getAllFromProductId(productId: String): LiveData<List<EntityShopsWithPurchases>>

}