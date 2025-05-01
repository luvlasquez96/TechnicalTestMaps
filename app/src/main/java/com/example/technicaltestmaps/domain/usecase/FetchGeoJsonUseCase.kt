package com.example.technicaltestmaps.domain.usecase

import com.example.technicaltestmaps.domain.repository.GeoJsonRepository
import com.mapbox.geojson.FeatureCollection

class FetchGeoJsonUseCase(private val repository: GeoJsonRepository) {
    suspend operator fun invoke(): FeatureCollection? {
        return repository.fetchGeoJson()
    }
}