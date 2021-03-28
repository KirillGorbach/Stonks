package com.example.stonks.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [FavoriteTickerEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun favoriteTickerDao(): FavoriteTickerDao

    companion object {
        var instance: AppDatabase? = null
        fun getAppDatabase(context: Context): AppDatabase? {
            if (instance==null) {
                synchronized(AppDatabase::class) {
                    instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "AppDatabase").build()
                }
            }
            return instance
        }

        fun destroyDatabase() {
            instance = null
        }
    }
}