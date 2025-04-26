package com.example.technicaltestmaps.data.local

import com.example.technicaltestmaps.domain.model.FavoritePoint
import com.example.technicaltestmaps.domain.repository.FavoritePointRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritePointRepositoryImpl(
    private val dao: FavoritePointDao
) : FavoritePointRepository {
    override suspend fun addFavoritePoint(point: FavoritePoint) {
        dao.insertPoint(point.toEntity())
    }

    override fun getAllFavoritePoints(): Flow<List<FavoritePoint>> {
        return dao.getAllPoints().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun deleteFavoritePoint(id: Int) {
        dao.deletePointById(id)
    }
}