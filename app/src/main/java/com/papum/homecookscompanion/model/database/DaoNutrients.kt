package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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

	@Query("""
        SELECT *
        FROM Nutrients
		WHERE idProduct IN (:ids)
    """)
	@Transaction
	fun getAllFromId_value(ids: List<Long>): List<EntityNutrients>


	/* insert */

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertOne(nutrients: EntityNutrients)

}