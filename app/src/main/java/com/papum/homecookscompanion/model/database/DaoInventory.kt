package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DaoInventory {


	/* insert */

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertOne(inventoryProduct: EntityInventory): Long

	/* delete */

	@Delete
	fun deleteOne(inventoryProduct: EntityInventory)

	/* update */
	@Update
	fun updateOne(inventoryProduct: EntityInventory)

}