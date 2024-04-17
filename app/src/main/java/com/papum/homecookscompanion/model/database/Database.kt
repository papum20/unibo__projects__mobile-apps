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
		EntityEdible::class,
		EntityFood::class,
		EntityIngredient::class,
		EntityInventory::class,
		EntityList::class,
		EntityNonEdible::class,
		EntityPlan::class,
		EntityProduct::class,
		EntityRecipe::class,
   ],
	version = 1,
	exportSchema = false
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

	abstract fun daoEdible()        	: DaoEdible
	abstract fun daoFood()          	: DaoFood
	abstract fun daoIngredient()		: DaoIngredient
	abstract fun daoInventory()			: DaoInventory
	abstract fun daoList()				: DaoList
	abstract fun daoNonEdible()			: DaoNonEdible
	abstract fun daoPlan()				: DaoPlan
	abstract fun daoProduct()			: DaoProduct
	abstract fun daoRecipe()			: DaoRecipe


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

					daoProduct?.insertFood("plant", null, null)
					daoProduct?.insertFood("cereal", "plant", null)
					daoProduct?.insertFood("bread", "cereal", null)
					daoProduct?.insertFood("pasta", "cereal", null)
					daoProduct?.insertFood("recipe", null, null)
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