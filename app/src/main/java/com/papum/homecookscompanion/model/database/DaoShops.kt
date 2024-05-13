package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DaoShops
{

	/* query */

	@Query("SELECT * FROM Shops ")
	fun getAll(): LiveData<List<EntityShops>>

	@Query("SELECT * FROM Shops WHERE brand LIKE '%'||:pattern||'%'")
	@Transaction
	fun getAllMatches_onBrand(pattern: String): LiveData<List<EntityShops>>

	/* insert */

	@Insert
	fun insertOne(shop: EntityShops): Long


}