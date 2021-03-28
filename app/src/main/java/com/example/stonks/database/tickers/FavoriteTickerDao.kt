package com.example.stonks.database.tickers

import androidx.room.*

@Dao
interface FavoriteTickerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTicker(ticker: FavoriteTickerEntity)

    @Query("DELETE FROM Favorites WHERE ticker = :tickerName")
    fun deleteTicker(tickerName: String)

    @Query("SELECT * FROM Favorites")
    fun getFavorites(): List<FavoriteTickerEntity>
}