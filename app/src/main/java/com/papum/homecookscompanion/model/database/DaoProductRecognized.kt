package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DaoProductRecognized
{


	/* insert */

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	fun insertMany(productsRecognized: List<EntityProductRecognized>)


}