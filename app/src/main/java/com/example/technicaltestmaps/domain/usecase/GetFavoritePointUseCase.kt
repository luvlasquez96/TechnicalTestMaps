package com.example.technicaltestmaps.domain.usecase

import com.example.technicaltestmaps.domain.model.FavoritePoint
import com.example.technicaltestmaps.domain.repository.FavoritePointRepository
import kotlinx.coroutines.flow.Flow

class GetFavoritePointUseCase(private val repository: FavoritePointRepository) {
    operator fun invoke(): Flow<List<FavoritePoint>> = repository.getAllFavoritePoints()
}