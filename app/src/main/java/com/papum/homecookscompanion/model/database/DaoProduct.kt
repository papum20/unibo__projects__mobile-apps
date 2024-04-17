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

	@Insert
	fun _insertFood(product: EntityFood): Long

	@Transaction
	fun insertFood(name: String, parent: String?, brand: String?) {
		val product_id = insertProduct(
			EntityProduct(0, name, parent)
		)
		_insertFood(
			EntityFood(product_id, brand)
		)
	}



	/* delete */

	@Delete
	fun delete(user: EntityProduct)

}