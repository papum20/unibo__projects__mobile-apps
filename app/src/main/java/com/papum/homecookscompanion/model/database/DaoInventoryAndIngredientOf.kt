package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DaoInventoryAndIngredientOf {

	/* query */

	@Query("""
        SELECT *
        FROM Inventory
        INNER JOIN IngredientOf ON Inventory.idProduct = IngredientOf.idIngredient
		WHERE idRecipe = :id
    """)
	@Transaction
	fun getAllFromRecipeId_value(id: Long): List<EntityInventoryAndIngredientOf>




}