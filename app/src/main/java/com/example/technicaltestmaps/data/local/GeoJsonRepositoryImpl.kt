package com.example.technicaltestmaps.data.local

import com.example.technicaltestmaps.domain.repository.GeoJsonRepository
import com.mapbox.geojson.FeatureCollection
import okhttp3.OkHttpClient
import okhttp3.Request

class GeoJsonRepositoryImpl : GeoJsonRepository {

    private val client = OkHttpClient()

    override suspend fun fetchGeoJson(): FeatureCollection? {
        val request = Request.Builder()
            .url("https://d2ad6b4ur7yvpq.cloudfront.net/naturalearth-3.3.0/ne_50m_populated_places_simple.geojson")
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string()?.let { jsonString ->
                    FeatureCollection.fromJson(jsonString)
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}