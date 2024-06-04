package com.papum.homecookscompanion.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

@Dao
interface DaoMeals {

	/* insert */

	@Insert
	fun insertOne(mealsProduct: EntityMeals)

	/* delete */

	@Delete
	fun deleteOne(mealsProduct: EntityMeals)

	/* update */
	@Update
	fun updateOne(meal: EntityMeals)

}