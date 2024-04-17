package com.papum.homecookscompanion.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

/*
@Dao
interface DaoProductAndFood {

	/* GET */

	@Query("SELECT * FROM Food")
	fun getAll(): List<EntityFood>

	/* INSERT */

	@Insert
	fun _insertFood(food: EntityFood)

	@Insert
	fun _insertProduct(product: EntityProduct): Long

    @Transaction
    fun insertProductAndFood(name: String, parent: String?, brand: String?) {
        val product_id = _insertProduct(
			EntityProduct(0, name, parent)
		)
        _insertFood(
			EntityFood(product_id, brand)
		)
    }
}
*/