package com.papum.homecookscompanion.model.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
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
	version = 28,
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
					val daoAlerts				= INSTANCE?.daoAlerts()
					val daoProduct				= INSTANCE?.daoProduct()
					val daoInventory			= INSTANCE?.daoInventory()
					val daoList					= INSTANCE?.daoList()
					val daoPurchases			= INSTANCE?.daoPurchases()
					val daoProductAndNutrients	= INSTANCE?.daoProductAndNutrients()
					val daoProductRecognized	= INSTANCE?.daoProductRecognized()
					val daoShops				= INSTANCE?.daoShops()

					val idPlant		= daoProductAndNutrients?.insertProductAndNutrients(
							EntityProduct(0, "plant", null,		isEdible=true, isRecipe=false),
							EntityNutrients(0, null, null, null, null)
						)
					val idGarlic	= daoProductAndNutrients?.insertProductAndNutrients(
							EntityProduct(0, "garlic", "plant",	isEdible=true, isRecipe=false),
							EntityNutrients(0, 80F, 18F, 0.4F, 1F)
						)
					val idCereal	= daoProductAndNutrients?.insertProductAndNutrients(
							EntityProduct(0, "cereal", "plant",	isEdible=true, isRecipe=false),
							EntityNutrients(0, null, null, null, null)
						)
					val idBread		= daoProductAndNutrients?.insertProductAndNutrients(
							EntityProduct(0, "bread", "cereal",	isEdible=true, isRecipe=false),
							EntityNutrients(0, 200F, 40F, 10F, 5F)
						)
					val idPasta		= daoProductAndNutrients?.insertProductAndNutrients(
							EntityProduct(0, "pasta", "cereal",	isEdible=true, isRecipe=false),
							EntityNutrients(0, null, null, null, null)
						)
					val idRecipe	= daoProductAndNutrients?.insertProductAndNutrients(
							EntityProduct(0, "recipe", null,		isEdible=true, isRecipe=true),
							EntityNutrients(0, null, null, null, null)
						)

					idBread?.let { id ->
						daoAlerts?.insertOne(EntityAlerts(id, 2F))
						daoInventory?.insertOne(EntityInventory(id, 1F))
						daoList?.insertOne(EntityList(id, 12F))
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
					idPlant?.let { id ->
						daoAlerts?.insertOne(EntityAlerts(id, 1F))
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
							"AGLIO 200g", idEurospin!!, idGarlic!!
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