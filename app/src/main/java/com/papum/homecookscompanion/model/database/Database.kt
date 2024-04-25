package com.papum.homecookscompanion.model.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
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
   ],
	version = 13,
	exportSchema = false
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

	abstract fun daoAlerts()    	    			: DaoAlerts
	abstract fun daoNutrients()    	    			: DaoNutrients
	abstract fun daoIngredient()					: DaoIngredientOf
	abstract fun daoInventory()						: DaoInventory
	abstract fun daoList()							: DaoList
	abstract fun daoMeals()							: DaoMeals
	abstract fun daoProduct()						: DaoProduct
	abstract fun daoProductAndInventory()			: DaoProductAndInventory
	abstract fun daoProductAndInventoryWithAlerts()	: DaoProductAndInventoryWithAlerts
	abstract fun daoProductAndList()				: DaoProductAndList
	abstract fun daoProductAndMeals()				: DaoProductAndMeals
	abstract fun daoProductAndNutrients()			: DaoProductAndNutrients
	abstract fun daoProductAndMealsWithNutrients()	: DaoProductAndMealsWithNutrients


	companion object {

		@Volatile
		private var INSTANCE: Database? = null

		private const val N_THREADS: Int = 4
		val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(N_THREADS)

		private val sRoomDatabaseCallback = object: RoomDatabase.Callback() {
			override fun onCreate(db: SupportSQLiteDatabase) {
				super.onCreate(db)

				/* What happens when the database gets called for the first time? */
				databaseWriteExecutor.execute() {
					val daoAlerts				= INSTANCE?.daoAlerts()
					val daoProduct				= INSTANCE?.daoProduct()
					val daoInventory			= INSTANCE?.daoInventory()
					val daoList					= INSTANCE?.daoList()
					val daoProductAndNutrients	= INSTANCE?.daoProductAndNutrients()

					val idPlant		= daoProductAndNutrients?.insertProductAndNutrients(
							EntityProduct(0, "plant", null,		isEdible=true, isRecipe=false),
							EntityNutrients(0, null, null, null, null)
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