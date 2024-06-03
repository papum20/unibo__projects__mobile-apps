package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DaoProductAndIngredientOf {

	/* query */

	@Query("""
        SELECT *
        FROM Product
        INNER JOIN IngredientOf ON Product.id = IngredientOf.idIngredient
		WHERE idRecipe = :id
    """)
	@Transaction
	fun getAllFromRecipeId(id: Long): LiveData<List<EntityProductAndIngredientOf>>

	@Query("""
        SELECT *
        FROM Product
        INNER JOIN IngredientOf ON Product.id = IngredientOf.idIngredient
		WHERE idRecipe = :id
    """)
	@Transaction
	fun getAllFromRecipeId_value(id: Long): List<EntityProductAndIngredientOf>

	/* insert */

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun _insertAllIngredients(ingredientsList: List<EntityIngredientOf>)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun _insertProduct(product: EntityProduct): Long

	@Transaction
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertRecipeAndIngredients(recipe: EntityProduct, ingredientsList: List<EntityIngredientOf>): Long {
		val id = _insertProduct(recipe)
		_insertAllIngredients(
			ingredientsList.map { it.apply {idRecipe = id} }
		)
		return id
	}


}