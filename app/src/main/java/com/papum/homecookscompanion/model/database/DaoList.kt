package com.papum.homecookscompanion.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DaoList
{


	/* query */

	@Query("SELECT * FROM list WHERE idProduct = :id")
	fun getOne_fromId(id: Long): EntityList?

	/* insert */

	@Insert
	fun insertOne(listProduct: EntityList): Long

	/* delete */

	@Delete
	fun deleteOne(listProduct: EntityList)

	/* update */
	@Update
	fun updateOne(listProduct: EntityList)

}