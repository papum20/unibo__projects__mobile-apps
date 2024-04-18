package com.papum.homecookscompanion.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoList
{

	/* insert */

	@Insert
	fun insertOne(listProduct: EntityList)

	/* delete */

	@Delete
	fun deleteOne(listProduct: EntityList)


}