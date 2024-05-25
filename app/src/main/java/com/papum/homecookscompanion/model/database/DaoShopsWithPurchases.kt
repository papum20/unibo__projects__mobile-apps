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
        FROM Purchases
        LEFT JOIN Shops ON Shops.id = Purchases.idShop
		WHERE idProduct = :productId
		GROUP BY idShop
    """)
	@Transaction
	fun getAllFromProductId(productId: Long): LiveData<List<EntityShopsWithPurchases>>

}