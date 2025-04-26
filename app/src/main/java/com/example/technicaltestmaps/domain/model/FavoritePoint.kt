package com.example.technicaltestmaps.domain.model

data class FavoritePoint(
    val id: Int = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val type: PointType
)

enum class PointType {
    NORMAL,
    ALERT
}
