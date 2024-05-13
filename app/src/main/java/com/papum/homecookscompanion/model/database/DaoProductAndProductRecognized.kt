package com.papum.homecookscompanion.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DaoProductAndProductRecognized
{

	/* query */

	@Query("""
		SELECT *
		FROM Product
		INNER JOIN ProductRecognized ON Product.id = ProductRecognized.idProduct
		WHERE idShop = :shopId AND recognizedText IN (:recognizedTexts) 
	""")
	fun getAll_fromTextAndShop(recognizedTexts: List<String>, shopId: Long): LiveData<List<EntityProductAndProductRecognized>>



}