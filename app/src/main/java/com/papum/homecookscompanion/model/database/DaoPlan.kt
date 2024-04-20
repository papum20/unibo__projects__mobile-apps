package com.papum.homecookscompanion.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoPlan {

	/* insert */

	@Insert
	fun insertOne(planProduct: EntityPlan)

	/* delete */

	@Delete
	fun deleteOne(planProduct: EntityPlan)

}