package com.papum.homecookscompanion.model

import android.app.Application
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
		var daoEdible		= db.daoEdible()
		var daoFood  		= db.daoFood()
		var daoIngredient	= db.daoIngredient()
		var daoInventory	= db.daoInventory()
		var daoList			= db.daoList()
		var daoNonEdible	= db.daoNonEdible()
		var daoPlan			= db.daoPlan()
		var daoProduct		= db.daoProduct()
		var daoRecipe		= db.daoRecipe()
	}



}