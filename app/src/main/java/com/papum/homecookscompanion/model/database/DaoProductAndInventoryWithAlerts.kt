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

	/**
	 * Get all products either in inventory or alerts (not necessarily in both).
	 */
	@Transaction
	fun getAll(): List<EntityProductAndInventoryWithAlerts> {
		return getAllInAlerts() + getAllInInventory()
	}

	@Query("""
        SELECT *
		FROM Alerts
		LEFT JOIN Inventory ON Alerts.idProduct = Inventory.idProduct
        LEFT JOIN Product ON Alerts.idProduct = Product.id
    """)
	fun getAllInAlerts(): List<EntityProductAndInventoryWithAlerts>

	@Query("""
        SELECT *
		FROM Inventory
		LEFT JOIN Alerts ON Inventory.idProduct = Alerts.idProduct
        LEFT JOIN Product ON Inventory.idProduct = Product.id
    """)
	fun getAllInInventory(): List<EntityProductAndInventoryWithAlerts>

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