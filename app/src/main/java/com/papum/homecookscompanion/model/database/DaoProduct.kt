package com.papum.homecookscompanion.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoProduct {

	@Insert
	fun insert(vararg food: EntityProduct): Long

	@Delete
	fun delete(user: EntityProduct)

	@Query("SELECT * FROM Product")
	fun getAll(): List<EntityProduct>

}