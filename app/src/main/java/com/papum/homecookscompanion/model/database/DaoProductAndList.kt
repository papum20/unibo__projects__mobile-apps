package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface DaoProductAndList {

	/* query */

	@Query("""
        SELECT *
        FROM Product
        INNER JOIN List ON Product.id = List.idProduct
    """)
	fun getAll(): LiveData<List<EntityProductAndList>>

	@Query("""
        SELECT *
        FROM Product
        INNER JOIN List ON Product.id = List.idProduct
		WHERE idProduct = :id
    """)
	fun getAllFromId(id: String): LiveData<List<EntityProductAndList>>

}