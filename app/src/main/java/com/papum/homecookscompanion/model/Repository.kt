package com.papum.homecookscompanion.model

import android.app.Application
import androidx.lifecycle.LiveData
import com.papum.homecookscompanion.model.database.DaoIngredientOf
import com.papum.homecookscompanion.model.database.DaoInventory
import com.papum.homecookscompanion.model.database.DaoList
import com.papum.homecookscompanion.model.database.DaoNutrients
import com.papum.homecookscompanion.model.database.DaoPlan
import com.papum.homecookscompanion.model.database.DaoProduct
import com.papum.homecookscompanion.model.database.DaoProductAndInventory
import com.papum.homecookscompanion.model.database.Database
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndInventory

class Repository(app: Application) {

	var daoEdible				: DaoNutrients
	var daoIngredient			: DaoIngredientOf
	var daoInventory			: DaoInventory
	var daoList					: DaoList
	var daoPlan					: DaoPlan
	var daoProduct				: DaoProduct
	var daoProductAndInventory	: DaoProductAndInventory

	init {
		val db = Database.getDatabase(app)
		daoEdible				= db.daoNutrients()
		daoIngredient			= db.daoIngredient()
		daoInventory			= db.daoInventory()
		daoList					= db.daoList()
		daoPlan					= db.daoPlan()
		daoProduct				= db.daoProduct()
		daoProductAndInventory	= db.daoProductAndInventory()
	}

	/* Get */

	fun getAllProducts(): LiveData<List<EntityProduct>> {
		return daoProduct.getAll()
	}

	fun getAllProductsWithInventory(): LiveData<List<EntityProductAndInventory>> {
		return daoProductAndInventory.getAll()
	}

	/* Insert */

	fun insertProduct(product: EntityProduct) {
		Database.databaseWriteExecutor.execute {
			daoProduct.insertProduct(product)
		}
	}

	fun insertInInventory(inventoryProduct: EntityInventory) {
		Database.databaseWriteExecutor.execute {
			daoInventory.insertOne(inventoryProduct)
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