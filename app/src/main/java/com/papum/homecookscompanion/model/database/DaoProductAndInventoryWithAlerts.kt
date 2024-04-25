package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DaoProductAndInventoryWithAlerts {

	/* query */

	@Query("""
        SELECT *
        FROM Product
		INNER JOIN Alerts ON Product.id = Alerts.idProduct
        LEFT JOIN Inventory ON Product.id = Inventory.idProduct
    """)
	fun getAll(): List<EntityProductAndInventoryWithAlerts>

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
	fun getAllLowStocks(): List<EntityProductAndInventoryWithAlerts>

}