package com.papum.homecookscompanion.model

import android.app.Application
import androidx.lifecycle.LiveData
import com.papum.homecookscompanion.model.database.DaoEdible
import com.papum.homecookscompanion.model.database.DaoFood
import com.papum.homecookscompanion.model.database.DaoIngredient
import com.papum.homecookscompanion.model.database.DaoInventory
import com.papum.homecookscompanion.model.database.DaoList
import com.papum.homecookscompanion.model.database.DaoNonEdible
import com.papum.homecookscompanion.model.database.DaoPlan
import com.papum.homecookscompanion.model.database.DaoProduct
import com.papum.homecookscompanion.model.database.DaoRecipe
import com.papum.homecookscompanion.model.database.Database
import com.papum.homecookscompanion.model.database.EntityFood
import com.papum.homecookscompanion.model.database.EntityProduct

class Repository(app: Application) {

	var daoEdible		: DaoEdible
	var daoFood  		: DaoFood
	var daoIngredient	: DaoIngredient
	var daoInventory	: DaoInventory
	var daoList			: DaoList
	var daoNonEdible	: DaoNonEdible
	var daoPlan			: DaoPlan
	var daoProduct		: DaoProduct
	var daoRecipe		: DaoRecipe

	init {
		val db = Database.getDatabase(app)
		daoEdible		= db.daoEdible()
		daoFood  		= db.daoFood()
		daoIngredient	= db.daoIngredient()
		daoInventory	= db.daoInventory()
		daoList			= db.daoList()
		daoNonEdible	= db.daoNonEdible()
		daoPlan			= db.daoPlan()
		daoProduct		= db.daoProduct()
		daoRecipe		= db.daoRecipe()
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