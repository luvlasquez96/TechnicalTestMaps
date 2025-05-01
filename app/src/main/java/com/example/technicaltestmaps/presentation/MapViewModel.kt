package com.example.technicaltestmaps.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.technicaltestmaps.domain.model.FavoritePoint
import com.example.technicaltestmaps.domain.model.PointType
import com.example.technicaltestmaps.domain.repository.FavoritePointRepository
import com.example.technicaltestmaps.domain.repository.GeoJsonRepository
import com.example.technicaltestmaps.domain.usecase.AddFavoritePointUseCase
import com.example.technicaltestmaps.domain.usecase.DeleteFavoritePointUseCase
import com.example.technicaltestmaps.domain.usecase.FetchGeoJsonUseCase
import com.example.technicaltestmaps.domain.usecase.GetFavoritePointUseCase
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val addFavoritePointUseCase: AddFavoritePointUseCase,
    private val deleteFavoritePointUseCase: DeleteFavoritePointUseCase,
    private val getFavoritePointUseCase: GetFavoritePointUseCase,
    private val fetchGeoJsonUseCase: FetchGeoJsonUseCase
) : ViewModel() {

    private val _favoritePoints = MutableStateFlow<List<FavoritePoint>>(emptyList())
    val favoritePoints: StateFlow<List<FavoritePoint>> = _favoritePoints.asStateFlow()

    private val _featureCollection = MutableStateFlow<FeatureCollection?>(null)
    val featureCollection: StateFlow<FeatureCollection?> = _featureCollection.asStateFlow()

    private val _userLocation = MutableStateFlow<Point?>(null)
    val userLocation: StateFlow<Point?> = _userLocation.asStateFlow()

    private val _selectedFavoritePoint = MutableStateFlow<FavoritePoint?>(null)
    val selectedFavoritePoint: StateFlow<FavoritePoint?> = _selectedFavoritePoint.asStateFlow()

    private val _pendingPointToSave = MutableStateFlow<Point?>(null)
    val pendingPointToSave: StateFlow<Point?> = _pendingPointToSave.asStateFlow()

    init {
        loadFavoritePoints()
        loadGeoJson()
    }

    private fun loadFavoritePoints() {
        viewModelScope.launch {
            getFavoritePointUseCase().collect { points ->
                _favoritePoints.value = points
            }
        }
    }

    private fun loadGeoJson() {
        viewModelScope.launch {
            val result = fetchGeoJsonUseCase()
            _featureCollection.value = result
        }
    }

    fun onMapLongClick(point: Point) {
        _pendingPointToSave.value = point
    }

    fun saveFavoritePoint(name: String, type: PointType) {
        val point = _pendingPointToSave.value ?: return
        viewModelScope.launch {
            val newPoint = FavoritePoint(
                id = 0,
                name = name,
                latitude = point.latitude(),
                longitude = point.longitude(),
                type = type
            )
            addFavoritePointUseCase(newPoint)
            _pendingPointToSave.value = null
        }
    }

    fun selectFavoritePoint(point: FavoritePoint?) {
        _selectedFavoritePoint.value = point
    }

    fun deletePoint(id: Int) {
        viewModelScope.launch {
            deleteFavoritePointUseCase(id)
            if (_selectedFavoritePoint.value?.id == id) {
                _selectedFavoritePoint.value = null
            }
        }
    }

    fun updateUserLocation(location: Point) {
        _userLocation.value = location
    }
}