package com.papum.homecookscompanion.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoShops
{

	/* insert */

	@Insert
	fun insertOne(shop: EntityShops)


}