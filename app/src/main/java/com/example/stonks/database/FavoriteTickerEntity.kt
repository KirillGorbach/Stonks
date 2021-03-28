package com.example.stonks.database

import androidx.room.Entity


@Entity(tableName = "Favorites")
data class FavoriteTickerEntity(val ticker: String)