package com.papum.homecookscompanion.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoFood {
	@Insert
	fun insertAll(vararg users: EntityFood)

	@Delete
	fun delete(user: EntityFood)

	@Query("SELECT * FROM Food")
	fun getAll(): List<EntityFood>
}