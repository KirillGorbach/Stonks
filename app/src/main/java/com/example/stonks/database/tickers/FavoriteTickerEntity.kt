package com.example.stonks.database.tickers

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Favorites")
data class FavoriteTickerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ticker: String)
