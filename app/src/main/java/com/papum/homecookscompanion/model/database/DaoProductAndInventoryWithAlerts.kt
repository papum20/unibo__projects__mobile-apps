package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DaoProductAndInventoryWithAlerts {

	/* query */

	@Query("""
        SELECT *
		FROM Alerts
		LEFT JOIN Inventory ON Alerts.idProduct = Inventory.idProduct
        LEFT JOIN Product ON Alerts.idProduct = Product.id
    """)
	@Transaction
	fun getAllInAlerts(): LiveData<List<EntityProductAndInventoryWithAlerts>>

	@Query("""
        SELECT *
		FROM Inventory
		LEFT JOIN Alerts ON Inventory.idProduct = Alerts.idProduct
        LEFT JOIN Product ON Inventory.idProduct = Product.id
    """)
	@Transaction
	fun getAllInInventory(): LiveData<List<EntityProductAndInventoryWithAlerts>>

	/**
	 * Get all items with stock lower than alerts.
	 */
	@Query("""
        SELECT *
        FROM Product
		INNER JOIN Alerts ON Product.id = Alerts.idProduct
        LEFT JOIN Inventory ON Product.id = Inventory.idProduct
		WHERE Inventory.quantity IS NULL OR Inventory.quantity < Alerts.quantity
    """)
	@Transaction
	fun getAllLowStocks_value(): List<EntityProductAndInventoryWithAlerts>

}