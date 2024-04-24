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
		EntityNutrients::class,
		EntityIngredientOf::class,
		EntityInventory::class,
		EntityList::class,
		EntityMeals::class,
		EntityProduct::class,
   ],
	version = 8,
	exportSchema = false
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

	abstract fun daoNutrients()    	    			: DaoNutrients
	abstract fun daoIngredient()					: DaoIngredientOf
	abstract fun daoInventory()						: DaoInventory
	abstract fun daoList()							: DaoList
	abstract fun daoMeals()							: DaoMeals
	abstract fun daoProduct()						: DaoProduct
	abstract fun daoProductAndInventory()			: DaoProductAndInventory
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
					val daoProduct		= INSTANCE?.daoProduct()
					val daoInventory	= INSTANCE?.daoInventory()
					val daoList			= INSTANCE?.daoList()

					val idPlant		= daoProduct?.insertProduct(EntityProduct(0, "plant", null,		isEdible=true, isRecipe=false))
					val idCereal	= daoProduct?.insertProduct(EntityProduct(0, "cereal", "plant",	isEdible=true, isRecipe=false))
					val idBread		= daoProduct?.insertProduct(EntityProduct(0, "bread", "cereal",	isEdible=true, isRecipe=false))
					val idPasta		= daoProduct?.insertProduct(EntityProduct(0, "pasta", "cereal",	isEdible=true, isRecipe=false))
					val idRecipe	= daoProduct?.insertProduct(EntityProduct(0, "recipe", null,		isEdible=true, isRecipe=true))

					idBread?.let { id ->
						daoInventory?.insertOne(EntityInventory(id, 1F))
					}
					idPasta?.let { id ->
						daoInventory?.insertOne(EntityInventory(id, 2.5F))
					}
					idBread?.let { id ->
						daoList?.insertOne(EntityList(id, 12F))
					}
					idPasta?.let { id ->
						daoList?.insertOne(EntityList(id, 15F))
					}
					idCereal?.let { id ->
						daoList?.insertOne(EntityList(id, 1F))
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