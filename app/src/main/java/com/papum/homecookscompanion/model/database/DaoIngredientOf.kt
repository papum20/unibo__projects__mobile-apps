package com.papum.homecookscompanion.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DaoIngredientOf {

	/* Insert */

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(ingredients: List<EntityIngredientOf>)

	/* Delete */

	@Query("DELETE FROM IngredientOf WHERE idRecipe = :idRecipe")
	fun deleteMatchingRecipe(idRecipe: Long)

}