package com.papum.homecookscompanion.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.papum.homecookscompanion.model.database.DaoAlerts
import com.papum.homecookscompanion.model.database.DaoIngredientOf
import com.papum.homecookscompanion.model.database.DaoInventory
import com.papum.homecookscompanion.model.database.DaoList
import com.papum.homecookscompanion.model.database.DaoMeals
import com.papum.homecookscompanion.model.database.DaoNutrients
import com.papum.homecookscompanion.model.database.DaoPurchases
import com.papum.homecookscompanion.model.database.DaoProduct
import com.papum.homecookscompanion.model.database.DaoProductAndIngredientOf
import com.papum.homecookscompanion.model.database.DaoProductAndInventory
import com.papum.homecookscompanion.model.database.DaoProductAndInventoryWithAlerts
import com.papum.homecookscompanion.model.database.DaoProductAndList
import com.papum.homecookscompanion.model.database.DaoProductAndMeals
import com.papum.homecookscompanion.model.database.DaoProductAndMealsWithNutrients
import com.papum.homecookscompanion.model.database.DaoProductAndNutrients
import com.papum.homecookscompanion.model.database.DaoProductAndProductRecognized
import com.papum.homecookscompanion.model.database.DaoProductRecognized
import com.papum.homecookscompanion.model.database.DaoShops
import com.papum.homecookscompanion.model.database.DaoShopsWithPurchases
import com.papum.homecookscompanion.model.database.Database
import com.papum.homecookscompanion.model.database.EntityAlerts
import com.papum.homecookscompanion.model.database.EntityIngredientOf
import com.papum.homecookscompanion.model.database.EntityInventory
import com.papum.homecookscompanion.model.database.EntityList
import com.papum.homecookscompanion.model.database.EntityMeals
import com.papum.homecookscompanion.model.database.EntityNutrients
import com.papum.homecookscompanion.model.database.EntityProduct
import com.papum.homecookscompanion.model.database.EntityProductAndIngredientOf
import com.papum.homecookscompanion.model.database.EntityProductAndInventoryWithAlerts
import com.papum.homecookscompanion.model.database.EntityProductAndList
import com.papum.homecookscompanion.model.database.EntityProductAndMeals
import com.papum.homecookscompanion.model.database.EntityProductAndMealsWithNutrients
import com.papum.homecookscompanion.model.database.EntityProductAndNutrients
import com.papum.homecookscompanion.model.database.EntityProductAndProductRecognized
import com.papum.homecookscompanion.model.database.EntityProductRecognized
import com.papum.homecookscompanion.model.database.EntityPurchases
import com.papum.homecookscompanion.model.database.EntityShops
import com.papum.homecookscompanion.model.database.EntityShopsWithPurchases
import com.papum.homecookscompanion.utils.UtilProducts
import com.papum.homecookscompanion.utils.errors.BadQuantityException
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.jvm.Throws

class Repository(app: Context) {

	private var daoAlerts							: DaoAlerts
	private var daoIngredientOf						: DaoIngredientOf
	private var daoInventory						: DaoInventory
	private var daoList								: DaoList
	private var daoMeals							: DaoMeals
	private var daoNutrients						: DaoNutrients
	private var daoPurchases						: DaoPurchases
	private var daoProduct							: DaoProduct
	private var daoProductAndIngredientOf			: DaoProductAndIngredientOf
	private var daoProductAndInventory				: DaoProductAndInventory
	private var daoProductAndInventoryWithAlerts	: DaoProductAndInventoryWithAlerts
	private var daoProductAndList					: DaoProductAndList
	private var daoProductAndMeals					: DaoProductAndMeals
	private var daoProductAndNutrients				: DaoProductAndNutrients
	private var daoProductAndMealsWithNutrients		: DaoProductAndMealsWithNutrients
	private var daoProductAndProductRecognized		: DaoProductAndProductRecognized
	private var daoProductRecognized				: DaoProductRecognized
	private var daoShops							: DaoShops
	private var daoShopsWithPurchases				: DaoShopsWithPurchases

	init {
		val db = Database.getDatabase(app)
		daoAlerts							= db.daoAlerts()
		daoIngredientOf						= db.daoIngredientOf()
		daoInventory						= db.daoInventory()
		daoList								= db.daoList()
		daoMeals							= db.daoMeals()
		daoNutrients						= db.daoNutrients()
		daoPurchases						= db.daoPurchases()
		daoProduct							= db.daoProduct()
		daoProductAndIngredientOf			= db.daoProductAndIngredientOf()
		daoProductAndInventory				= db.daoProductAndInventory()
		daoProductAndInventoryWithAlerts	= db.daoProductAndInventoryWithAlerts()
		daoProductAndList					= db.daoProductAndList()
		daoProductAndMeals					= db.daoProductAndMeals()
		daoProductAndNutrients				= db.daoProductAndNutrients()
		daoProductAndMealsWithNutrients		= db.daoProductAndMealsWithNutrients()
		daoProductAndProductRecognized		= db.daoProductAndProductRecognized()
		daoProductRecognized				= db.daoProductRecognized()
		daoShops							= db.daoShops()
		daoShopsWithPurchases				= db.daoShopsWithPurchases()
	}

	/* Get */

	fun getNutrients(ids: List<Long>): LiveData<List<EntityNutrients>> {
		return daoNutrients.getAllFromId(ids)
	}

	fun getNutrients_value(ids: List<Long>): List<EntityNutrients> {
		return daoNutrients.getAllFromId_value(ids)
	}

	fun getAllProducts(): LiveData<List<EntityProduct>> {
		return daoProduct.getAll()
	}

	fun getAllProducts_fromSubstr_caseInsensitive(substr: String): LiveData<List<EntityProduct>> {
		return daoProduct.getAllMatches_lowercase("%${substr.lowercase()}%")
	}

	fun getAllIngredients_fromRecipeId(recipeId: Long): LiveData<List<EntityProductAndIngredientOf>> {
		return daoProductAndIngredientOf.getAllFromRecipeId(recipeId)
	}

	fun getAllIngredients_fromRecipeId_value(recipeId: Long): List<EntityProductAndIngredientOf> {
		return daoProductAndIngredientOf.getAllFromRecipeId_value(recipeId)
	}

	fun getMatchingProductsRecognized(recognizedTexts: List<String>, shopId: Long): LiveData<List<EntityProductAndProductRecognized>> {
		return daoProductAndProductRecognized.getAll_fromTextAndShop(recognizedTexts, shopId)
	}


	/**
	 * Get all products either in inventory or alerts (not necessarily in both).
	 */
	fun getAllInventoryWithAlerts(): LiveData<List<EntityProductAndInventoryWithAlerts>> =
		daoProductAndInventoryWithAlerts.getAllInAlerts().switchMap { inAlerts ->
			daoProductAndInventoryWithAlerts.getAllInInventory().switchMap { inInventory ->
				MutableLiveData(
					inAlerts + inInventory.filter { product ->
						inAlerts.find { alert ->
							alert.alert?.idProduct == product.inventoryItem?.idProduct
						} == null
					}
				)

		}
		/*
		val products = MutableLiveData<List<EntityProductAndInventoryWithAlerts>>()
		Database.databaseWriteExecutor.execute {
			val alerts = daoProductAndInventoryWithAlerts.getAllInAlerts_value()
			products.postValue(
				alerts + daoProductAndInventoryWithAlerts.getAllInInventory_value().filter { product ->
					alerts.find { alert ->
						alert.alert?.idProduct == product.inventoryItem?.idProduct
					} == null
				}
			)
		}
		return products
		 */
	}


	fun getInventory_lowStock_value(): List<EntityProductAndInventoryWithAlerts> {
		return daoProductAndInventoryWithAlerts.getAllLowStocks_value()
	}

	fun getAllList(): LiveData<List<EntityProductAndList>> {
		return daoProductAndList.getAll()
	}

	fun getAllMeals(): LiveData<List<EntityProductAndMeals>> {
			return daoProductAndMeals.getAll()
	}

	fun getMealsAndNutrients(start: LocalDateTime, end: LocalDateTime): LiveData<List<EntityProductAndMealsWithNutrients>> {
		return daoProductAndMealsWithNutrients.getAllFromDateTimeInterval(
			start.toInstant(ZoneOffset.UTC).toEpochMilli(),
			end.toInstant(ZoneOffset.UTC).toEpochMilli()
		)
	}

	fun getAllShops(): LiveData<List<EntityShops>> {
		return daoShops.getAll()
	}

	fun getAllShops_value(): List<EntityShops> {
		return daoShops.getAll_value()
	}

	/**
	 * Match shops on a substring of brand name.
 	 */
	fun getMatchingShops(brandSubstr: String): LiveData<List<EntityShops>> {
		return daoShops.getAllMatches_onBrand(brandSubstr)
	}

	fun getPurchases_forProduct(productId: Long): LiveData<List<EntityShopsWithPurchases>> {
		return daoShopsWithPurchases.getAllFromProductId(productId)
	}

	fun getProduct(id: Long): LiveData<EntityProduct?> {
		return daoProduct.getOneFromId(id.toString())
	}

	fun getProduct_value(id: Long): EntityProduct? {
		return daoProduct.getOneFromId_value(id.toString())
	}

	fun getProductFromName_value(name: String, parent: String): EntityProduct? {
		return daoProduct.getOneFromName_value(name, parent)
	}

	fun getProductWithNutrients(id: Long): LiveData<EntityProductAndNutrients> {
		return daoProductAndNutrients.getOneFromId(id.toString())
	}


	/* Insert */

	fun insertAllIngredients(ingredients: List<EntityIngredientOf>) {
		Database.databaseWriteExecutor.execute {
			daoIngredientOf.insertAll(ingredients)
		}
	}

	fun addAlert(alert: EntityAlerts): EntityAlerts {
		var newId: Long = alert.idProduct
		Database.databaseWriteExecutor.execute {
			newId = daoAlerts.insertOne(alert)
		}
		return alert.apply {
			idProduct = newId
		}
	}

	fun addListItem(listProduct: EntityList): EntityList {
		var newId: Long = listProduct.idProduct
		Database.databaseWriteExecutor.execute {
			newId = daoList.insertOne(listProduct)
		}
		return listProduct.apply {
			idProduct = newId
		}
	}
	

	fun addMeal(mealsProduct: EntityMeals) {
		Database.databaseWriteExecutor.execute {
			daoMeals.insertOne(mealsProduct)
		}
	}

	/**
	 * (Re)insert quantity from meal to inventory.
	 * Update quantity of meal (or delete from meals if quantity=0),
	 * then (if didn't give error) add to inventory.
	 * Update quantity to null if null.
	 */
	@Throws(BadQuantityException::class)
	fun insertMealBackToInventory(meal: EntityMeals, newQuantity: Float) {

		if(newQuantity >= meal.quantity)
			throw BadQuantityException("New quantity must be lower than current quantity")

		val oldQuantity	= meal.quantity
		meal.quantity	= newQuantity

		Database.databaseWriteExecutor.execute {
			if(meal.quantity <= 0F) daoMeals.deleteOne(meal)
			else daoMeals.updateOne(meal)

			addInventoryItemQuantity(EntityInventory(
				idProduct = meal.idEdible,
				quantity =  oldQuantity - newQuantity
			))
		}
	}

	/**
	 * Update quantity from inventory (or delete if quantity=0),
	 * removing meal.quantity from inventory.quantity,
	 * then (if didn't give error) add to meals.
	 * Update quantity to null if null.
	 */
	fun insertMealFromInventory(meal: EntityMeals, inventoryProduct: EntityInventory) {
		Database.databaseWriteExecutor.execute {
			if(inventoryProduct.quantity - meal.quantity <= 0F)
				daoInventory.deleteOne(inventoryProduct)
			else daoInventory.updateOne(inventoryProduct.apply{
				quantity -= meal.quantity
			})

			daoMeals.insertOne(meal)
		}
	}

	fun insertProductNonEdible(product: EntityProduct): Long {
		var newId = product.id
		Database.databaseWriteExecutor.execute {
			 newId = daoProduct.insertProduct(product)
		}
		return newId
	}
	suspend fun insertProduct_result(product: EntityProduct): Long {
		val newId: Long = daoProductAndNutrients.insertProductAndNutrients(product,
			EntityNutrients(0, null, null, null, null))
		return newId
	}

	fun insertManyProductsRecognized(productsRecognized: List<EntityProductRecognized>) {
		Database.databaseWriteExecutor.execute {
			daoProductRecognized.insertMany(productsRecognized)
		}
	}

	fun insertProductAndNutrients(product: EntityProduct, nutrients: EntityNutrients) {
		Database.databaseWriteExecutor.execute {
			daoProductAndNutrients.insertProductAndNutrients(product, nutrients)
		}
	}

	fun insertPurchase(purchase: EntityPurchases) {
		Database.databaseWriteExecutor.execute {
			daoPurchases.insertOne(purchase)
		}
	}

	fun insertManyPurchases(purchases: List<EntityPurchases>) {
		Database.databaseWriteExecutor.execute {
			daoPurchases.insertMany(purchases)
		}
	}

	/**
	 * Insert recipe in Products, and ingredients in IngredientOf.
	 * ALso insert nutrients.
	 * Also replace recipe if already present.
	 */
	fun insertRecipeAndIngredients(recipe: EntityProduct, ingredients: List<EntityIngredientOf>) {
		Database.databaseWriteExecutor.execute {
			// first delete old ingredients, then insert new ones (if id=0 doesn't do anything)
			daoIngredientOf.deleteMatchingRecipe(recipe.id)
			val newId = daoProductAndIngredientOf.insertRecipeAndIngredients(recipe, ingredients)

			val nutrientsList = daoNutrients.getAllFromId_value(
				ingredients.map { it.idIngredient }
			)

			daoNutrients.insertOne(
				UtilProducts.getRecipeNutrients(newId, ingredients, nutrientsList)
			)
		}
	}

	fun insertShop(shop: EntityShops) {
		Database.databaseWriteExecutor.execute {
			daoShops.insertOne(shop)
		}
	}

	/* Delete */

	fun deleteAlert(id: Long) {
		Database.databaseWriteExecutor.execute {
			daoAlerts.deleteOne_withId(id.toString())
		}
	}

	fun deleteInventoryItem(inventoryItem: EntityInventory) {
		Database.databaseWriteExecutor.execute {
			daoInventory.deleteOne(inventoryItem)
		}
	}

	fun deleteListItem(listItem: EntityList) {
		Database.databaseWriteExecutor.execute {
			daoList.deleteOne(listItem)
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


	fun updateAlert(alert: EntityAlerts) {
		Database.databaseWriteExecutor.execute {
			daoAlerts.updateOne(alert)
		}
	}

	fun updateInventoryItem(inventoryItem: EntityInventory) {
		Database.databaseWriteExecutor.execute {
			daoInventory.updateOne(inventoryItem)
		}
	}
	fun updateListItem(listItem: EntityList) {
		Database.databaseWriteExecutor.execute {
			daoList.updateOne(listItem)
		}
	}

	/**
	 * Sum quantity if not null and already exists in inventory, otherwise add.
	 */
	fun addInventoryItemQuantity(inventoryItem: EntityInventory): EntityInventory {

		val res: EntityInventory = inventoryItem
		Database.databaseWriteExecutor.execute {
			val fetchedItem = daoInventory.getOne_fromId(inventoryItem.idProduct)
			if (fetchedItem == null) {
				val newId = daoInventory.insertOne(inventoryItem)
				res.idProduct = newId
			} else {
				fetchedItem.quantity += inventoryItem.quantity
				daoInventory.updateOne(fetchedItem)
			}
		}
		return res
	}

	/**
	 * Sum quantity if not null and already exists in inventory, otherwise add.
	 */
	fun addListItemQuantity(listItem: EntityList) {
		Database.databaseWriteExecutor.execute {
			val fetchedItem = daoList.getOne_fromId(listItem.idProduct)
			if (fetchedItem == null) {
				daoList.insertOne(listItem)
			} else if (fetchedItem.quantity == null) {
				daoList.updateOne(listItem)
			} else if (listItem.quantity != null) {
				daoList.updateOne(
					fetchedItem.apply {
						quantity = quantity!! + listItem.quantity!!
					}
				)
			}

		}
	}

	fun updateProduct(product: EntityProduct) {
		Database.databaseWriteExecutor.execute {
			daoProduct.updateOne(product)
		}
	}


}