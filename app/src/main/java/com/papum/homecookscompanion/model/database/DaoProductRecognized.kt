package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoProductRecognized
{


	/* insert */

	@Insert
	fun insertOne(productRecognized: EntityProductRecognized)


}