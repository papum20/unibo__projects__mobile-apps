package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DaoProduct {

	/* query */

	@Query("SELECT * FROM Product")
	fun getAll(): LiveData<List<EntityProduct>>

	/* insert */

	@Insert
	fun insertProduct(product: EntityProduct): Long

	/* delete */

	@Delete
	fun delete(user: EntityProduct)

}