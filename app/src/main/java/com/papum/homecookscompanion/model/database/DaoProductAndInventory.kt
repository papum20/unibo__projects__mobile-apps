package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DaoProductAndInventory {

	/* query */

	@Transaction
	@Query("SELECT * FROM Inventory Product")
	fun getAll(): LiveData<List<EntityProductAndInventory>>

}