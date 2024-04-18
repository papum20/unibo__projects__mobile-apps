package com.papum.homecookscompanion.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert

@Dao
interface DaoInventory {

	/* insert */

	@Insert
	fun insertOne(inventoryProduct: EntityInventory)

	/* delete */

	@Delete
	fun deleteOne(inventoryProduct: EntityInventory)

}