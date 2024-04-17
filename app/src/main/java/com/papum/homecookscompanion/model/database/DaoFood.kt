package com.papum.homecookscompanion.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoFood {

	@Insert
	fun insertAll(vararg foods: EntityFood)

	@Delete
	fun delete(food: EntityFood)

	@Query("SELECT * FROM Food")
	fun getAll(): List<EntityFood>

}