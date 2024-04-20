package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface DaoProductAndPlan {

	/* query */

	@Query("""
        SELECT *
        FROM Product
        INNER JOIN Plan ON Product.id = Plan.idEdible
    """)
	fun getAll(): LiveData<List<EntityProductAndPlan>>

	@Query("""
        SELECT *
        FROM Product
        INNER JOIN Plan ON Product.id = Plan.idEdible
		WHERE idEdible = :id
    """)
	fun getAllFromId(id: String): LiveData<List<EntityProductAndPlan>>

}