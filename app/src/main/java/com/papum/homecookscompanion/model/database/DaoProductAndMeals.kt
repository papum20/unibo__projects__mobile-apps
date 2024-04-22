package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface DaoProductAndMeals {

	/* query */

	@Query("""
        SELECT *
        FROM Product
        INNER JOIN Meals ON Product.id = Meals.idEdible
    """)
	fun getAll(): LiveData<List<EntityProductAndMeals>>

	@Query("""
        SELECT *
        FROM Product
        INNER JOIN Meals ON Product.id = Meals.idEdible
		WHERE idEdible = :id
    """)
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
	fun getAllFromDateTimeInterval(start: Long, end: Long): LiveData<List<EntityProductAndMeals>>

}