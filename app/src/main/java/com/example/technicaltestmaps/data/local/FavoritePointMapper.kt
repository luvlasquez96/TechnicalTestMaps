package com.example.technicaltestmaps.data.local

import com.example.technicaltestmaps.domain.model.FavoritePoint
import com.example.technicaltestmaps.domain.model.PointType


fun FavoritePointEntity.toDomain(): FavoritePoint =
    FavoritePoint(id, name, latitude, longitude, PointType.valueOf(type))

fun FavoritePoint.toEntity(): FavoritePointEntity =
    FavoritePointEntity(id, name, latitude, longitude, type.name)