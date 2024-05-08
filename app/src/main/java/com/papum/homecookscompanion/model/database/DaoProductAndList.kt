package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DaoProductAndList {

	/* query */

	@Query("""
        SELECT *
        FROM Product
        INNER JOIN List ON Product.id = List.idProduct
    """)
	@Transaction
	fun getAll(): LiveData<List<EntityProductAndList>>

	@Query("""
        SELECT *
        FROM Product
        INNER JOIN List ON Product.id = List.idProduct
		WHERE idProduct = :id
    """)
	@Transaction
	fun getAllFromId(id: String): LiveData<List<EntityProductAndList>>

}