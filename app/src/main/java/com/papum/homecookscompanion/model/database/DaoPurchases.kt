package com.papum.homecookscompanion.model.database

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface DaoPurchases
{

	/* insert */

	@Insert
	fun insertOne(purchase: EntityPurchases)


}