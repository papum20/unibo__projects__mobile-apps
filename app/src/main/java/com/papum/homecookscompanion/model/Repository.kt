package com.papum.homecookscompanion.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.papum.homecookscompanion.model.database.DaoAlerts
import com.papum.homecookscompanion.model.database.DaoIngredientOf
import com.papum.homecookscompanion.model.database.DaoInventory
import com.papum.homecookscompanion.model.database.DaoList
import com.papum.homecookscompanion.model.database.DaoMeals
import com.papum.homecookscompanion.model.database.DaoPurchases
import com.papum.homecookscompanion.model.database.DaoProduct
import com.papum.homecookscompanion.model.database.DaoProductAndInventory
import com.papum.homecookscompanion.model.database.DaoProductAndInventoryWithAlerts
import com.papum.homecookscompanion.model.database.DaoProductAndList
import com.papum.homecookscompanion.model.database.DaoProductAndMeals
import com.papum.homecookscompanion.model.database.DaoProductAndMealsWithNutrients
import com.papum.homecookscompanion.model.database.DaoProductAndNutrients
import com.papum.homecookscompanion.model.database.DaoShops
import com.papum.homecookscompanion.model.database.DaoShopsWithPurchases
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
import com.papum.homecookscompanion.model.database.EntityPurchases
import com.papum.homecookscompanion.model.database.EntityShops
import com.papum.homecookscompanion.model.database.EntityShopsWithPurchases
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class Repository(app: Context) {

	private var daoAlerts							: DaoAlerts
	private var daoIngredient						: DaoIngredientOf
	private var daoInventory						: DaoInventory
	private var daoList								: DaoList
	private var daoMeals							: DaoMeals
	private var daoPurchases						: DaoPurchases
	private var daoProduct							: DaoProduct
	private var daoProductAndInventory				: DaoProductAndInventory
	private var daoProductAndInventoryWithAlerts	: DaoProductAndInventoryWithAlerts
	private var daoProductAndList					: DaoProductAndList
	private var daoProductAndMeals					: DaoProductAndMeals
	private var daoProductAndNutrients				: DaoProductAndNutrients
	private var daoProductAndMealsWithNutrients		: DaoProductAndMealsWithNutrients
	private var daoShops							: DaoShops
	private var daoShopsWithPurchases				: DaoShopsWithPurchases

	init {
		val db = Database.getDatabase(app)
		daoAlerts							= db.daoAlerts()
		daoIngredient						= db.daoIngredient()
		daoInventory						= db.daoInventory()
		daoList								= db.daoList()
		daoMeals							= db.daoMeals()
		daoPurchases						= db.daoPurchases()
		daoProduct							= db.daoProduct()
		daoProductAndInventory				= db.daoProductAndInventory()
		daoProductAndInventoryWithAlerts	= db.daoProductAndInventoryWithAlerts()
		daoProductAndList					= db.daoProductAndList()
		daoProductAndMeals					= db.daoProductAndMeals()
		daoProductAndNutrients				= db.daoProductAndNutrients()
		daoProductAndMealsWithNutrients		= db.daoProductAndMealsWithNutrients()
		daoShops							= db.daoShops()
		daoShopsWithPurchases				= db.daoShopsWithPurchases()
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

	/**
	 * Get all products either in inventory or alerts (not necessarily in both).
	 */
	fun getAllProductsWithInventoryAndAlerts(): LiveData<List<EntityProductAndInventoryWithAlerts>> {

		val products = MutableLiveData<List<EntityProductAndInventoryWithAlerts>>()
		Database.databaseWriteExecutor.execute {
			val alerts = daoProductAndInventoryWithAlerts.getAllInAlerts()
			products.postValue(
				alerts + daoProductAndInventoryWithAlerts.getAllInInventory().filter { product ->
					alerts.find { alert ->
						alert.alert?.idProduct == product.inventoryItem?.idProduct
					} == null
				}
			)
		}
		return products
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

	fun getAllShopsAndPurchases_fromProductId(productId: Long): LiveData<List<EntityShopsWithPurchases>> {
		return daoShopsWithPurchases.getAllFromProductId(productId.toString())
	}

	fun getProduct_fromId(id: Long): LiveData<EntityProduct> {
		return daoProduct.getOneFromId(id.toString())
	}

	fun getProductWithNutrients_fromId(id: Long): LiveData<EntityProductAndNutrients> {
		return daoProductAndNutrients.getOneFromId(id.toString())
	}


	/* Insert */

	fun insertAlert(alert: EntityAlerts): EntityAlerts {
		var newId: Long = alert.idProduct
		Database.databaseWriteExecutor.execute {
			newId = daoAlerts.insertOne(alert)
		}
		return alert.apply {
			idProduct = newId
		}
	}

	fun insertInInventory(inventoryProduct: EntityInventory): EntityInventory {
		var newId: Long = inventoryProduct.idProduct
		Database.databaseWriteExecutor.execute {
			newId = daoInventory.insertOne(inventoryProduct)
		}
		return inventoryProduct.apply {
			idProduct = newId
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

	/**
	 * Update quantity from inventory (or delete if quantity=0),
	 * then (if didn't give error) add to meals.
	 * Update quantity to null if null.
	 */
	fun insertMealFromInventory(mealsProduct: EntityMeals, inventoryProduct: EntityInventory) {
		Database.databaseWriteExecutor.execute {
			inventoryProduct.quantity?.let { q ->
				if(q <= 0F) daoInventory.deleteOne(inventoryProduct)
				else daoInventory.updateOne(inventoryProduct)
			}
			daoMeals.insertOne(mealsProduct)
		}
	}

	fun insertPurchase(purchase: EntityPurchases) {
		Database.databaseWriteExecutor.execute {
			daoPurchases.insertOne(purchase)
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

	fun insertShop(shop: EntityShops) {
		Database.databaseWriteExecutor.execute {
			daoShops.insertOne(shop)
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

	/* Update */

	fun updateInventoryItem(inventoryItem: EntityInventory) {
		Database.databaseWriteExecutor.execute {
			daoInventory.updateOne(inventoryItem)
		}
	}

	fun updateAlert(alert: EntityAlerts) {
		Database.databaseWriteExecutor.execute {
			daoAlerts.updateOne(alert)
		}
	}


}