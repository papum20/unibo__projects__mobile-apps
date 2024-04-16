package com.papum.homecookscompanion.model.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
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
abstract class Database : RoomDatabase() {
    abstract fun daoFood(): DaoFood

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
                    val dao = INSTANCE?.daoFood()

                    dao?.insertAll(
                        EntityFood("plants", null, null),
                        EntityFood("cereals", "plants", null),
                        EntityFood("bread", "cereals", null),
                        EntityFood("pasta", "cereals", null),
                    )
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