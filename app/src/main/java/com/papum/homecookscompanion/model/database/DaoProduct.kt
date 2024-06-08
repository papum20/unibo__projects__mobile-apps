package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DaoProduct {

	/* query */

	@Query("select * from Product where name LIKE :pattern")
	fun getAllMatches(pattern: String): LiveData<List<EntityProduct>>

	@Query("""
        SELECT *
        FROM Product
		WHERE id = :id
    """)
	fun getOneFromId(id: String): LiveData<EntityProduct?>

	@Query("""
        SELECT *
        FROM Product
		WHERE id = :id
    """)
	fun getOneFromId_value(id: String): EntityProduct?

	@Query("""
        SELECT *
        FROM Product
		WHERE name = :name AND parent = :parent
    """)
	fun getOneFromName_value(name: String, parent: String): EntityProduct?


	/**
	 * Use LOWER(name)
	 */
	@Query("select * from Product where LOWER(name) LIKE :pattern")
	fun getMatches_lowercase(pattern: String): LiveData<List<EntityProduct>>

	@Query("SELECT * FROM Product WHERE isRecipe = 1 AND LOWER(name) LIKE :pattern")
	fun getMatchesRecipes_lowercase(pattern: String): LiveData<List<EntityProduct>>


	/* insert */

	@Insert
	fun insertProduct(product: EntityProduct): Long

	/* delete */

	@Delete
	fun delete(product: EntityProduct)

	/* update */

	@Update
	fun updateOne(product: EntityProduct)


}