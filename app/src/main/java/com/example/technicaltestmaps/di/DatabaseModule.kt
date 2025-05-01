package com.example.technicaltestmaps.di

import android.app.Application
import androidx.room.Room
import com.example.technicaltestmaps.data.local.FavoritePointDao
import com.example.technicaltestmaps.data.local.FavoritePointRepositoryImpl
import com.example.technicaltestmaps.data.local.GeoJsonRepositoryImpl
import com.example.technicaltestmaps.data.local.TechnicalTestMapsDatabase
import com.example.technicaltestmaps.domain.repository.FavoritePointRepository
import com.example.technicaltestmaps.domain.repository.GeoJsonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): TechnicalTestMapsDatabase =
        Room.databaseBuilder(
            app,
            TechnicalTestMapsDatabase::class.java,
            "map_app_db"
        ).build()

    @Provides
    fun provideFavoritePointDao(db: TechnicalTestMapsDatabase): FavoritePointDao =
        db.favoritePointDao()

    @Provides
    @Singleton
    fun provideFavoritePointRepository(dao: FavoritePointDao): FavoritePointRepository =
        FavoritePointRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideGeoJsonRepository(): GeoJsonRepository =
        GeoJsonRepositoryImpl()
}