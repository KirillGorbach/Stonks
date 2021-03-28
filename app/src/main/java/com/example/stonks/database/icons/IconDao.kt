package com.example.stonks.database.icons

import androidx.room.*


@Dao
interface IconDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIcon(icon: IconEntity)

    @Query("DELETE FROM Icons WHERE ticker = :iconName")
    fun deleteIcon(iconName: String)

    @Query("SELECT * FROM Icons")
    fun getIcons(): List<IconEntity>
}