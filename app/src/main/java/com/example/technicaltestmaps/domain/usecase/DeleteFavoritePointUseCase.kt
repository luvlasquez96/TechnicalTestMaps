package com.example.technicaltestmaps.domain.usecase

import com.example.technicaltestmaps.domain.repository.FavoritePointRepository

class DeleteFavoritePointUseCase(private val repository: FavoritePointRepository) {
    suspend operator fun invoke(id: Int) = repository.deleteFavoritePoint(id)
}