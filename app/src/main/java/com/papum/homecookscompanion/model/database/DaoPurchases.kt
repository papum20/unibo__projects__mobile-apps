package com.papum.homecookscompanion.model.database

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface DaoPurchases
{

	/* insert */

	@Insert
	fun insertMany(purchases: List<EntityPurchases>)

	@Insert
	fun insertOne(purchase: EntityPurchases)


}