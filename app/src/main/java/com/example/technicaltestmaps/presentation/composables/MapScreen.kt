package com.example.technicaltestmaps.presentation.composables

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.technicaltestmaps.domain.model.FavoritePoint
import com.example.technicaltestmaps.domain.model.PointType
import com.example.technicaltestmaps.presentation.MapViewModel
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onShowPointOnMap: (FavoritePoint) -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    LaunchedEffect(Unit) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val point = Point.fromLngLat(it.longitude, it.latitude)
                    viewModel.updateUserLocation(point)
                }
            }
        }
    }

    val points by viewModel.favoritePoints.collectAsState()
    val pendingPoint by viewModel.pendingPointToSave.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val selectedPoint by viewModel.selectedFavoritePoint.collectAsState()
    val featureCollection by viewModel.featureCollection.collectAsState()
    var showFavorites by remember { mutableStateOf(true) }

    var showDialog by remember { mutableStateOf(false) }
    var pointName by remember { mutableStateOf("") }
    var pointType by remember { mutableStateOf(PointType.NORMAL) }

    LaunchedEffect(pendingPoint) {
        if (pendingPoint != null) {
            showDialog = true
        }
    }

    if (showDialog) {
        AddPointDialog(
            pointName = pointName,
            pointType = pointType,
            onNameChange = { pointName = it },
            onTypeChange = { pointType = it },
            onDismiss = {
                showDialog = false
                pointName = ""
                pointType = PointType.NORMAL
            },
            onConfirm = {
                viewModel.saveFavoritePoint(pointName, pointType)
                showDialog = false
                pointName = ""
                pointType = PointType.NORMAL
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            MapView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (showFavorites) 0.6f else 1f),
                points = points,
                userLocation = userLocation,
                selectedPoint = selectedPoint,
                featureCollection = featureCollection,
                onMapLongClick = { mapboxPoint ->
                    viewModel.onMapLongClick(mapboxPoint)
                }
            )

            if (showFavorites && points.isNotEmpty()) {
                FavoritesScreen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.4f),
                    points = points,
                    onShowOnMap = { point ->
                        viewModel.selectFavoritePoint(point)
                        onShowPointOnMap(point)
                        showFavorites = false
                    },
                    onDelete = { id -> viewModel.deletePoint(id) }
                )
            }
        }

        if (!showFavorites) {
            FloatingActionButton(
                onClick = { showFavorites = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Mostrar favoritos")
            }
        }
    }
}