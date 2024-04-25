package com.papum.homecookscompanion.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoAlerts
{

	/* insert */

	@Insert
	fun insertOne(alert: EntityAlerts)

	/* delete */

	@Query("DELETE FROM Alerts WHERE idProduct = :id")
	fun deleteOne_withId(id: String)

}