package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DaoProductAndMealsWithNutrients {

	/* query */

	/**
	 * Get all entries where date is after `start` and before `end` (extremes included).
	 * `start` and `end`, just like `date` column, are milliseconds from the epoch.
	 */
	@Query(
		"""
        SELECT *
        FROM Product
        INNER JOIN Meals ON Product.id = Meals.idEdible
		INNER JOIN Nutrients ON Product.id = Nutrients.idProduct
		WHERE date >= :start AND date <= :end
    """
	)
	fun getAllFromDateTimeInterval_withNutrients(start: Long, end: Long): LiveData<List<EntityProductAndMealsWithNutrients>>


}