package com.papum.homecookscompanion.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface DaoIngredientOf {

	/* Insert */

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(ingredients: List<EntityIngredientOf>)

}