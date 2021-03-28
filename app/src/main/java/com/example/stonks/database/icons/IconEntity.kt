package com.example.stonks.database.icons

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Icons")
data class IconEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ticker: String,
    val source: String
)