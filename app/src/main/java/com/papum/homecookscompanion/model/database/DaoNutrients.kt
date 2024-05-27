package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DaoNutrients {

	@Query("""
        SELECT *
        FROM Nutrients
		WHERE idProduct IN (:ids)
    """)
	@Transaction
	fun getAllFromId(ids: List<Long>): LiveData<List<EntityNutrients>>

}