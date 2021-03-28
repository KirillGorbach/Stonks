package com.example.stonks.database

import androidx.room.*

@Dao
interface FavoriteTickerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTicker(ticker: FavoriteTickerEntity)

    @Delete
    fun deleteTicker(ticker: FavoriteTickerEntity)

    @Query("SELECT * FROM Favorites")
    fun getFavorites(): List<FavoriteTickerEntity>
}