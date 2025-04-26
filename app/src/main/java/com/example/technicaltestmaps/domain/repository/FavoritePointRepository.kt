package com.example.technicaltestmaps.domain.repository

import com.example.technicaltestmaps.domain.model.FavoritePoint
import kotlinx.coroutines.flow.Flow

interface FavoritePointRepository {
    suspend fun addFavoritePoint(point: FavoritePoint)
    fun getAllFavoritePoints(): Flow<List<FavoritePoint>>
    suspend fun deleteFavoritePoint(id: Int)
}