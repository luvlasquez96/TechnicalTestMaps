package com.example.technicaltestmaps.di

import com.example.technicaltestmaps.domain.repository.FavoritePointRepository
import com.example.technicaltestmaps.domain.repository.GeoJsonRepository
import com.example.technicaltestmaps.domain.usecase.AddFavoritePointUseCase
import com.example.technicaltestmaps.domain.usecase.DeleteFavoritePointUseCase
import com.example.technicaltestmaps.domain.usecase.FetchGeoJsonUseCase
import com.example.technicaltestmaps.domain.usecase.GetFavoritePointUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideAddFavoritePointUseCase(repository: FavoritePointRepository) =
        AddFavoritePointUseCase(repository)

    @Provides
    fun provideDeleteFavoritePointUseCase(repository: FavoritePointRepository) =
        DeleteFavoritePointUseCase(repository)

    @Provides
    fun provideGetFavoritePointUseCase(repository: FavoritePointRepository) =
        GetFavoritePointUseCase(repository)

    @Provides
    fun provideFetchGeoJsonUseCase(repository: GeoJsonRepository) =
        FetchGeoJsonUseCase(repository)
}