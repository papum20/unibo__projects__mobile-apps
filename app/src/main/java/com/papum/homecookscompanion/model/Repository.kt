package com.papum.homecookscompanion.model

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.papum.homecookscompanion.model.database.DaoAlerts
import com.papum.homecookscompanion.model.database.DaoIngredientOf
import com.papum.homecookscompanion.model.database.DaoInventory
import com.papum.homecookscompanion.model.database.DaoList
import com.papum.homecookscompanion.model.database.DaoNutrients
import com.papum.homecookscompanion.model.database.DaoMeals
import com.papum.homecookscompanion.model.database.DaoProduct
import com.papum.homecookscompanion.model.database.DaoProductAndInventory
import com.papum.homecookscompanion.model.database.DaoProductAndInventoryWithAlerts
import com.papum.homecookscompanion.model.database.DaoProductAndList
import com.papum.homecookscompanion.model.database.DaoProductAndMeals
import com.papum.homecookscompanion.model.database.DaoProductAndMealsWithNutrients
import com.papum.homecookscompanion.model.database.DaoProductAndNutrients
import com.papum.homecookscompanion.model.database.Database
import com.papum.homecookscompanion.model.database.EntityAlerts
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityList
import com.papum.homecookscompanion.model.database.EntityMeals
import com.papum.homecookscompanion.model.database.EntityNutrients
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndInventory
import com.papum.homecookscompanion.model.database.EntityProductAndInventoryWithAlerts
import com.papum.homecookscompanion.model.database.EntityProductAndList
import com.papum.homecookscompanion.model.database.EntityProductAndMeals
import com.papum.homecookscompanion.model.database.EntityProductAndMealsWithNutrients
import com.papum.homecookscompanion.model.database.EntityProductAndNutrients
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class Repository(app: Context) {

	var daoAlerts							: DaoAlerts
	var daoIngredient						: DaoIngredientOf
	var daoInventory						: DaoInventory
	var daoList								: DaoList
	var daoMeals							: DaoMeals
	var daoProduct							: DaoProduct
	var daoProductAndInventory				: DaoProductAndInventory
	var daoProductAndInventoryWithAlerts	: DaoProductAndInventoryWithAlerts
	var daoProductAndList					: DaoProductAndList
	var daoProductAndMeals					: DaoProductAndMeals
	var daoProductAndNutrients				: DaoProductAndNutrients
	var daoProductAndMealsWithNutrients		: DaoProductAndMealsWithNutrients

	init {
		val db = Database.getDatabase(app)
		daoAlerts							= db.daoAlerts()
		daoIngredient						= db.daoIngredient()
		daoInventory						= db.daoInventory()
		daoList								= db.daoList()
		daoMeals							= db.daoMeals()
		daoProduct							= db.daoProduct()
		daoProductAndInventory				= db.daoProductAndInventory()
		daoProductAndInventoryWithAlerts	= db.daoProductAndInventoryWithAlerts()
		daoProductAndList					= db.daoProductAndList()
		daoProductAndMeals					= db.daoProductAndMeals()
		daoProductAndNutrients				= db.daoProductAndNutrients()
		daoProductAndMealsWithNutrients		= db.daoProductAndMealsWithNutrients()
	}

	/* Get */

	fun getAllProducts(): LiveData<List<EntityProduct>> {
		return daoProduct.getAll()
	}

	fun getAllProducts_fromSubstr_caseInsensitive(substr: String): LiveData<List<EntityProduct>> {
		return daoProduct.getAllMatches_lowercase("%${substr.lowercase()}%")
	}

	fun getAllProductsWithInventory(): LiveData<List<EntityProductAndInventory>> {
		return daoProductAndInventory.getAll()
	}

	fun getAllProductsWithInventory_fromId(id: Long): LiveData<List<EntityProductAndInventory>> {
		return daoProductAndInventory.getAllFromId(id.toString())
	}

	fun getAllProductsWithInventoryAndAlerts(): List<EntityProductAndInventoryWithAlerts> {
		return daoProductAndInventoryWithAlerts.getAll()
	}

	fun getAllProductsWithInventoryAndAlerts_lowStocks(): List<EntityProductAndInventoryWithAlerts> {
		return daoProductAndInventoryWithAlerts.getAllLowStocks()
	}

	fun getAllProductsWithList(): LiveData<List<EntityProductAndList>> {
		return daoProductAndList.getAll()
	}

	fun getAllProductsWithList_fromId(id: Long): LiveData<List<EntityProductAndList>> {
		return daoProductAndList.getAllFromId(id.toString())
	}

	fun getAllProductsWithMeals(): LiveData<List<EntityProductAndMeals>> {
			return daoProductAndMeals.getAll()
	}

	/**
	 * `month` from 1.
	 */
	fun getAllProductsWithMeals_fromDate(date: LocalDateTime): LiveData<List<EntityProductAndMeals>> {
		// set localDateTime to time=0 -> convert to instant (nanosecs from epoch) -> convert to millisecs
		val startOfDay	= date.with(LocalTime.MIN).toInstant(ZoneOffset.UTC).toEpochMilli()
		val endOfDay	= date.with(LocalTime.MAX).toInstant(ZoneOffset.UTC).toEpochMilli()
		return daoProductAndMeals.getAllFromDateTimeInterval(startOfDay, endOfDay)
	}

	/**
	 * `month` from 1.
	 */
	fun getAllProductsWithMealsAndNutrients_fromDate(date: LocalDateTime): LiveData<List<EntityProductAndMealsWithNutrients>> {
		// set localDateTime to time=0 -> convert to instant (nanosecs from epoch) -> convert to millisecs
		val startOfDay	= date.with(LocalTime.MIN).toInstant(ZoneOffset.UTC).toEpochMilli()
		val endOfDay	= date.with(LocalTime.MAX).toInstant(ZoneOffset.UTC).toEpochMilli()
		return daoProductAndMealsWithNutrients.getAllFromDateTimeInterval_withNutrients(startOfDay, endOfDay)
	}

	fun getProduct_fromId(id: Long): LiveData<EntityProduct> {
		return daoProduct.getOneFromId(id.toString())
	}

	fun getProductWithNutrients_fromId(id: Long): LiveData<EntityProductAndNutrients> {
		return daoProductAndNutrients.getOneFromId(id.toString())
	}


	/* Insert */

	fun insertAlert(alert: EntityAlerts) {
		Database.databaseWriteExecutor.execute {
			daoAlerts.insertOne(alert)
		}
	}

	fun insertInInventory(inventoryProduct: EntityInventory) {
		Database.databaseWriteExecutor.execute {
			daoInventory.insertOne(inventoryProduct)
		}
	}

	fun insertInList(listProduct: EntityList) {
		Database.databaseWriteExecutor.execute {
			daoList.insertOne(listProduct)
		}
	}

	fun insertInMeals(mealsProduct: EntityMeals) {
		Database.databaseWriteExecutor.execute {
			daoMeals.insertOne(mealsProduct)
		}
	}

	fun insertProduct(product: EntityProduct) {
		Database.databaseWriteExecutor.execute {
			daoProduct.insertProduct(product)
		}
	}

	fun insertProductAndNutrients(product: EntityProduct, nutrients: EntityNutrients) {
		Database.databaseWriteExecutor.execute {
			daoProductAndNutrients.insertProductAndNutrients(product, nutrients)
		}
	}

	/* Delete */

	fun deleteAlert_fromId(id: Long) {
		Database.databaseWriteExecutor.execute {
			daoAlerts.deleteOne_withId(id.toString())
		}
	}

	fun deleteProducts(products: List<EntityProduct>) {
		Database.databaseWriteExecutor.execute {
			products.forEach {
				daoProduct.delete(it)
			}
		}
	}


}