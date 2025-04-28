package com.example.technicaltestmaps.domain.repository

import com.mapbox.geojson.FeatureCollection

interface GeoJsonRepository {
    suspend fun fetchGeoJson(): FeatureCollection?
}