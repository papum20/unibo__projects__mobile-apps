package com.papum.homecookscompanion.model.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@androidx.room.Database(
	entities = [
		EntityNutrients::class,
		EntityIngredientOf::class,
		EntityInventory::class,
		EntityList::class,
		EntityPlan::class,
		EntityProduct::class,
   ],
	version = 2,
	exportSchema = false
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

	abstract fun daoNutrients()        	: DaoNutrients
	abstract fun daoIngredient()		: DaoIngredientOf
	abstract fun daoInventory()			: DaoInventory
	abstract fun daoList()				: DaoList
	abstract fun daoPlan()				: DaoPlan
	abstract fun daoProduct()			: DaoProduct


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
					val daoProduct = INSTANCE?.daoProduct()

					daoProduct?.insertProduct(EntityProduct(0, "plant", null,		isEdible=true, isRecipe=false))
					daoProduct?.insertProduct(EntityProduct(0, "cereal", "plant",	isEdible=true, isRecipe=false))
					daoProduct?.insertProduct(EntityProduct(0, "bread", "cereal",	isEdible=true, isRecipe=false))
					daoProduct?.insertProduct(EntityProduct(0, "pasta", "cereal",	isEdible=true, isRecipe=false))
					daoProduct?.insertProduct(EntityProduct(0, "recipe", null,		isEdible=true, isRecipe=false))
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