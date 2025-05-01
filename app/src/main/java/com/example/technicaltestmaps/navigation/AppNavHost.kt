package com.example.technicaltestmaps.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.technicaltestmaps.presentation.MapViewModel
import com.example.technicaltestmaps.presentation.composables.FavoritesScreen
import com.example.technicaltestmaps.presentation.composables.MapScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "map"
    ) {
        composable("map") {
            val viewModel: MapViewModel = hiltViewModel()

            MapScreen(
                viewModel = viewModel,
                onShowPointOnMap = { point ->
                    viewModel.selectFavoritePoint(point)
                }
            )
        }

        composable("favorites") {
            val viewModel: MapViewModel = hiltViewModel()
            val points by viewModel.favoritePoints.collectAsState()

            FavoritesScreen(
                points = points,
                onShowOnMap = { point ->
                    viewModel.selectFavoritePoint(point)
                    navController.navigate("map")
                },
                onDelete = { id ->
                    viewModel.deletePoint(id)
                }
            )
        }
    }
}