package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoProduct {

	/* query */

	@Query("SELECT * FROM Product")
	fun getAll(): List<EntityProduct>

	@Query("select * from Product where name LIKE :pattern")
	fun getAllMatches(pattern: String): List<EntityProduct>

	/* insert */

	@Insert
	fun insertProduct(product: EntityProduct): Long

	/* delete */

	@Delete
	fun delete(user: EntityProduct)

}