package com.example.technicaltestmaps.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.technicaltestmaps.domain.model.FavoritePoint
import com.example.technicaltestmaps.domain.model.PointType
import com.example.technicaltestmaps.presentation.MapViewModel

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onShowPointOnMap: (FavoritePoint) -> Unit
) {
    val points by viewModel.favoritePoints.collectAsState()
    val pendingPoint by viewModel.pendingPointToSave.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val selectedPoint by viewModel.selectedFavoritePoint.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var pointName by remember { mutableStateOf("") }

    LaunchedEffect(pendingPoint) {
        if (pendingPoint != null) {
            showDialog = true
        }
    }

    if (showDialog) {
        AddPointDialog(
            pointName = pointName,
            onNameChange = { pointName = it },
            onDismiss = {
                showDialog = false
                pointName = ""
            },
            onConfirm = {
                viewModel.saveFavoritePoint(pointName, PointType.NORMAL)
                showDialog = false
                pointName = ""
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        MapView(
            modifier = Modifier.weight(1f),
            points = points,
            userLocation = userLocation,
            selectedPoint = selectedPoint,
            onMapLongClick = { mapboxPoint ->
                viewModel.onMapLongClick(mapboxPoint)
            }
        )

        FavoritesScreen(
            points = points,
            onShowOnMap = { point ->
                viewModel.selectFavoritePoint(point)
                onShowPointOnMap(point)
            },
            onDelete = { id ->
                viewModel.deletePoint(id)
            }
        )
    }
}