package com.example.technicaltestmaps.domain.usecase

import com.example.technicaltestmaps.domain.model.FavoritePoint
import com.example.technicaltestmaps.domain.repository.FavoritePointRepository

class AddFavoritePointUseCase(private val repository: FavoritePointRepository) {
    suspend operator fun invoke(point: FavoritePoint) = repository.addFavoritePoint(point)
}