package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DaoProductAndMeals {

	/* query */

	@Query("""
        SELECT *
        FROM Product
        INNER JOIN Meals ON Product.id = Meals.idEdible
    """)
	@Transaction
	fun getAll(): LiveData<List<EntityProductAndMeals>>

	@Query("""
        SELECT *
        FROM Product
        INNER JOIN Meals ON Product.id = Meals.idEdible
		WHERE idEdible = :id
    """)
	@Transaction
	fun getAllFromId(id: String): LiveData<List<EntityProductAndMeals>>

	/**
	 * Get all entries where date is after `start` and before `end` (extremes included).
	 * `start` and `end`, just like `date` column, are milliseconds from the epoch.
	 */
	@Query("""
        SELECT *
        FROM Product
        INNER JOIN Meals ON Product.id = Meals.idEdible
		WHERE date >= :start AND date <= :end
    """)
	@Transaction
	fun getAllFromDateTimeInterval(start: Long, end: Long): LiveData<List<EntityProductAndMeals>>

}