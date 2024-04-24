package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DaoProductAndNutrients {

	/* query */

	@Query("""
        SELECT *
        FROM Product
        INNER JOIN List ON Product.id = List.idProduct
    """)
	fun getAll(): LiveData<List<EntityProductAndList>>

	/* insert */

	@Insert
	fun _insertNutrients(product: EntityNutrients)

	@Insert
	fun _insertProduct(product: EntityProduct): Long

	@Transaction
	@Insert
	fun insertProductAndNutrients(product: EntityProduct, nutrients: EntityNutrients) {
		val productId	= _insertProduct(product)
		nutrients.id	= productId
		_insertNutrients(nutrients)
	}

}