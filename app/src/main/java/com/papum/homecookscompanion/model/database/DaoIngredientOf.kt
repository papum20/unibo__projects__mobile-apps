package com.papum.homecookscompanion.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DaoIngredientOf {


	/* Query */

	@Query("""
        SELECT *
        FROM IngredientOf
		WHERE idRecipe = :id
    """)
	@Transaction
	fun getAllFromRecipeId_value(id: Long): List<EntityIngredientOf>


	/* Delete */

	@Query("DELETE FROM IngredientOf WHERE idRecipe = :idRecipe")
	fun deleteMatchingRecipe(idRecipe: Long)

}