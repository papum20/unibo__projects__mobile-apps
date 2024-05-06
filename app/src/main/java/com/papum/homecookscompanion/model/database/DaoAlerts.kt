package com.papum.homecookscompanion.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DaoAlerts
{

	/* insert */

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertOne(alert: EntityAlerts): Long

	/* delete */

	@Query("DELETE FROM Alerts WHERE idProduct = :id")
	fun deleteOne_withId(id: String)

	/* update */
	@Update
	fun updateOne(alert: EntityAlerts)

}