package com.papum.homecookscompanion.model.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.papum.homecookscompanion.utils.UtilProducts
import java.time.LocalDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@androidx.room.Database(
	entities = [
		EntityAlerts::class,
		EntityNutrients::class,
		EntityIngredientOf::class,
		EntityInventory::class,
		EntityList::class,
		EntityMeals::class,
		EntityProduct::class,
		EntityProductRecognized::class,
		EntityPurchases::class,
		EntityShops::class,
   ],
	version = 30,
	exportSchema = false
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

	abstract fun daoAlerts()    	    			: DaoAlerts
	abstract fun daoNutrients()    	    			: DaoNutrients
	abstract fun daoIngredientOf()					: DaoIngredientOf
	abstract fun daoInventory()						: DaoInventory
	abstract fun daoList()							: DaoList
	abstract fun daoMeals()							: DaoMeals
	abstract fun daoPurchases()						: DaoPurchases
	abstract fun daoProduct()						: DaoProduct
	abstract fun daoProductAndIngredientOf()			: DaoProductAndIngredientOf
	abstract fun daoProductAndInventory()			: DaoProductAndInventory
	abstract fun daoProductAndInventoryWithAlerts()	: DaoProductAndInventoryWithAlerts
	abstract fun daoProductAndList()				: DaoProductAndList
	abstract fun daoProductAndMeals()				: DaoProductAndMeals
	abstract fun daoProductAndNutrients()			: DaoProductAndNutrients
	abstract fun daoProductAndMealsWithNutrients()	: DaoProductAndMealsWithNutrients
	abstract fun daoProductAndProductRecognized()	: DaoProductAndProductRecognized
	abstract fun daoProductRecognized()				: DaoProductRecognized
	abstract fun daoShops()							: DaoShops
	abstract fun daoShopsWithPurchases()			: DaoShopsWithPurchases


	companion object {

		@Volatile
		private var INSTANCE: Database? = null

		private const val N_THREADS: Int = 4
		val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(N_THREADS)

		private val sRoomDatabaseCallback = object: RoomDatabase.Callback() {
			override fun onCreate(db: SupportSQLiteDatabase) {
				super.onCreate(db)

				/* What happens when the database gets called for the first time? */
				databaseWriteExecutor.execute {
					val daoAlerts					= INSTANCE?.daoAlerts()
					val daoProduct					= INSTANCE?.daoProduct()
					val daoInventory				= INSTANCE?.daoInventory()
					val daoList						= INSTANCE?.daoList()
					val daoMeals					= INSTANCE?.daoMeals()
					val daoNutrients				= INSTANCE?.daoNutrients()
					val daoPurchases				= INSTANCE?.daoPurchases()
					val daoProductAndIngredientOf	= INSTANCE?.daoProductAndIngredientOf()
					val daoProductAndNutrients		= INSTANCE?.daoProductAndNutrients()
					val daoProductRecognized		= INSTANCE?.daoProductRecognized()
					val daoShops					= INSTANCE?.daoShops()

					val idEdible	= daoProductAndNutrients?.insertProductAndNutrients(
							EntityProduct(0, "edible", "",		isEdible=true, isRecipe=false),
							EntityNutrients(0, null, null, null, null)
						)
					val idWater		= daoProductAndNutrients?.insertProductAndNutrients(
						EntityProduct(0, "water", "edible",	isEdible=true, isRecipe=false),
						EntityNutrients(0, 0F, 0F, 0F, 0F)
					)
					val idVegetable		= daoProductAndNutrients?.insertProductAndNutrients(
							EntityProduct(0, "vegetable", "edible",	isEdible=true, isRecipe=false),
							EntityNutrients(0, 33F, 7F, 0.1F, 1F)
						)
					val idTomato		= daoProductAndNutrients?.insertProductAndNutrients(
							EntityProduct(0, "tomato", "vegetable",	isEdible=true, isRecipe=false),
							EntityNutrients(0, 40F, 8F, 0.2F, 1.5F)
						)
					val idGarlic	= daoProductAndNutrients?.insertProductAndNutrients(
							EntityProduct(0, "garlic", "vegetable",	isEdible=true, isRecipe=false),
							EntityNutrients(0, 80F, 18F, 0.4F, 1F)
						)
					val idCereal	= daoProductAndNutrients?.insertProductAndNutrients(
							EntityProduct(0, "cereal", "edible",	isEdible=true, isRecipe=false),
							EntityNutrients(0, 366F, 69F, 5F, 11F)
						)
					val idBread		= daoProductAndNutrients?.insertProductAndNutrients(
							EntityProduct(0, "bread", "cereal",	isEdible=true, isRecipe=false),
							EntityNutrients(0, 200F, 40F, 10F, 5F)
						)
					val idPasta		= daoProductAndNutrients?.insertProductAndNutrients(
							EntityProduct(0, "pasta", "cereal",	isEdible=true, isRecipe=false),
							EntityNutrients(0, 366F, 69F, 5F, 11F)
						)
					val idMeat		= daoProductAndNutrients?.insertProductAndNutrients(
							EntityProduct(0, "meat", "edible",	isEdible=true, isRecipe=false),
							EntityNutrients(0, 154F, 0F, 8F, 20F)
						)
					val idPork		= daoProductAndNutrients?.insertProductAndNutrients(
							EntityProduct(0, "pork", "meat",	isEdible=true, isRecipe=false),
							EntityNutrients(0, 167F, 0F, 9F, 21F)
						)
					val idRecipe	= daoProductAndNutrients?.insertProductAndNutrients(
							EntityProduct(0, "recipe", "edible",	isEdible=true, isRecipe=true),
							EntityNutrients(0, null, null, null, null)
						)
					val idSoapDishes	= daoProduct?.insertProduct(
							EntityProduct(0, "for dishes", "soap",	isEdible=false, isRecipe=false)
						)
					val idHandkerchiefs	= daoProduct?.insertProduct(
							EntityProduct(0, "handkerchief", "paper",	isEdible=false, isRecipe=false)
						)


					idBread?.let { id ->
						daoAlerts?.insertOne(EntityAlerts(id, 2F))
						daoInventory?.insertOne(EntityInventory(id, 1F))
						daoList?.insertOne(EntityList(id, 12F))
						daoMeals?.insertOne(EntityMeals(0, id, LocalDateTime.now(), 100F))
					}
					idPasta?.let { id ->
						daoAlerts?.insertOne(EntityAlerts(id, 3F))
						daoInventory?.insertOne(EntityInventory(id, 2.5F))
						daoList?.insertOne(EntityList(id, 15F))
					}
					idCereal?.let { id ->
						daoAlerts?.insertOne(EntityAlerts(id, 1F))
						daoInventory?.insertOne(EntityInventory(id, 2F))
						daoList?.insertOne(EntityList(id, 1F))
					}
					idVegetable?.let { id ->
						daoAlerts?.insertOne(EntityAlerts(id, 1F))
					}
					idPork?.let { id ->
						daoMeals?.insertOne(EntityMeals(0, id, LocalDateTime.now(), 200F))
					}


					val idEurospin = daoShops?.insertOne(EntityShops(
						0,"via del Lavoro 8","Eurospin", "Bologna", "Italy",
						44.50305191918619, 11.361257401450482
					))

					val idLidl = daoShops?.insertOne(EntityShops(
						0,"via Libia","Lidl", "Bologna", "Italy",
						44.498491483993696, 11.366060902788908
					))


					val idConad = daoShops?.insertOne(EntityShops(
						0,"via Emilia","Conad", "Bologna", "Italy",
						44.484654040945024, 11.373957326156463
					))


					daoProductRecognized?.insertMany(
						listOf(
							EntityProductRecognized(
							"AGLIO 200g", idEurospin ?: 0, idGarlic ?: 0
							)
					))

					if(idBread != null && idEurospin != null && idLidl != null && idConad != null) {
						daoPurchases?.insertMany(
							listOf(
								EntityPurchases(0, idBread, idEurospin, LocalDateTime.now(), 2F),
								EntityPurchases(0, idBread, idEurospin, LocalDateTime.now(), 1.5F),
								EntityPurchases(0, idBread, idLidl, LocalDateTime.now(), 2F),
								EntityPurchases(0, idBread, idEurospin, LocalDateTime.now(), 3F),
								EntityPurchases(0, idBread, idLidl, LocalDateTime.now(), 2.3F),
								EntityPurchases(0, idBread, idConad, LocalDateTime.now(), 2.1F),
							)
						)
					}

					val ingredientsBreadAndGarlic = listOf(
						EntityIngredientOf(
							0, idBread ?: 0, 100F, 100F
						),
						EntityIngredientOf(
							0, idGarlic ?: 0, 20F, 20F
						)
					)
					// first delete old ingredients, then insert new ones (if id=0 doesn't do anything)
					daoProductAndIngredientOf?.insertRecipeAndIngredients(
						EntityProduct(
							0,"bread and garlic", "recipe", true, true
						), ingredientsBreadAndGarlic)?.let { newId ->
							daoNutrients?.getAllFromId_value(
								ingredientsBreadAndGarlic.map { it.idIngredient }
							)?.let { nutrientsList ->
								daoNutrients.insertOne(
									UtilProducts.getRecipeNutrients(
										newId,
										ingredientsBreadAndGarlic,
										nutrientsList
									)
								)
							}
					}


				}
			}
		}

		fun getDatabase(context: Context) : Database {
			return INSTANCE ?: synchronized (this) {
				val _INSTANCE = Room.databaseBuilder(
						context.applicationContext,
						Database::class.java,
						"todo_database"
					).addCallback(sRoomDatabaseCallback).build()
				INSTANCE = _INSTANCE
				_INSTANCE
			}
		}
	}

}