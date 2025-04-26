package com.example.technicaltestmaps.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoint(point: FavoritePointEntity)

    @Query("SELECT * FROM favorite_points")
    fun getAllPoints(): Flow<List<FavoritePointEntity>>

    @Query("DELETE FROM favorite_points WHERE id = :id")
    suspend fun deletePointById(id: Int)
}