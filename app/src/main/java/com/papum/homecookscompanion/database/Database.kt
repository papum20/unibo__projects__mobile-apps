package com.papum.homecookscompanion.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Entity1::class, Entity2::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun entity1Dao(): Entity1Dao
    abstract fun entity2Dao(): Entity2Dao
    abstract fun twoEntitiesDao(): TwoEntitiesDao
}