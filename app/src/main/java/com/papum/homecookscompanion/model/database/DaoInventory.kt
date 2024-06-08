package com.papum.homecookscompanion.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DaoInventory {


	/* query */

	@Query("SELECT * FROM inventory WHERE idProduct = :id")
	fun getFromId(id: Long): EntityInventory?

	@Query("SELECT * FROM inventory WHERE idProduct IN (:ids)")
	fun getManyFromId(ids: List<Long>): List<EntityInventory>

	/* insert */

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertOne(inventoryProduct: EntityInventory): Long

	/* delete */

	@Delete
	fun deleteMany(inventoryItems: List<EntityInventory>)

	@Delete
	fun deleteOne(inventoryProduct: EntityInventory)

	/* update */

	@Update
	fun updateMany(inventoryItems: List<EntityInventory>)
	@Update
	fun updateOne(inventoryProduct: EntityInventory)

}