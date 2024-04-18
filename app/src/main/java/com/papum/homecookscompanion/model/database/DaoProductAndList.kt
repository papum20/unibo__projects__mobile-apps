package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
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
	fun getAll(): LiveData<List<EntityProductAndList>>

}