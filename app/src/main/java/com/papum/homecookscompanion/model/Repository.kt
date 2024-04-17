package com.papum.homecookscompanion.model

import android.app.Application
import androidx.lifecycle.LiveData
import com.papum.homecookscompanion.model.database.DaoIngredientOf
import com.papum.homecookscompanion.model.database.DaoInventory
import com.papum.homecookscompanion.model.database.DaoList
import com.papum.homecookscompanion.model.database.DaoNutrients
import com.papum.homecookscompanion.model.database.DaoPlan
import com.papum.homecookscompanion.model.database.DaoProduct
import com.papum.homecookscompanion.model.database.Database
import com.papum.homecookscompanion.model.database.EntityProduct

class Repository(app: Application) {

	var daoEdible		: DaoNutrients
	var daoIngredient	: DaoIngredientOf
	var daoInventory	: DaoInventory
	var daoList			: DaoList
	var daoPlan			: DaoPlan
	var daoProduct		: DaoProduct

	init {
		val db = Database.getDatabase(app)
		daoEdible		= db.daoNutrients()
		daoIngredient	= db.daoIngredient()
		daoInventory	= db.daoInventory()
		daoList			= db.daoList()
		daoPlan			= db.daoPlan()
		daoProduct		= db.daoProduct()
	}

	/* Get */

	fun getAllProducts(): LiveData<List<EntityProduct>> {
		return daoProduct.getAll()
	}

	/* Insert */

	fun insertProduct(product: EntityProduct) {
		Database.databaseWriteExecutor.execute {
			daoProduct.insertProduct(product)
		}
	}

	/* Delete */

	fun deleteProducts(products: List<EntityProduct>) {
		Database.databaseWriteExecutor.execute {
			products.forEach {
				daoProduct.delete(it)
			}
		}
	}

}