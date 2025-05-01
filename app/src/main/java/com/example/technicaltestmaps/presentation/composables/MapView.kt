package com.example.technicaltestmaps.presentation.composables

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.example.technicaltestmaps.R
import com.example.technicaltestmaps.domain.model.FavoritePoint
import com.example.technicaltestmaps.domain.model.PointType
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.fillLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import timber.log.Timber

@Composable
fun MapView(
    modifier: Modifier = Modifier,
    points: List<FavoritePoint>,
    userLocation: Point?,
    selectedPoint: FavoritePoint?,
    featureCollection: FeatureCollection?,
    onMapLongClick: (Point) -> Unit,
    shouldCenterOnUser: Boolean,
    onUserCentered: () -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val annotationManager = remember(mapView) {
        mapView.annotations.createPointAnnotationManager()
    }
    val viewAnnotationManager = remember(mapView) {
        mapView.viewAnnotationManager
    }

    val currentSelectedPoint by rememberUpdatedState(selectedPoint)
    val currentUserLocation by rememberUpdatedState(userLocation)

    val styleLoaded = remember { mutableStateOf(false) }
    val hasUserMovedMap = remember { mutableStateOf(false) }

    AndroidView(
        modifier = modifier,
        factory = { mapView },
        update = { view ->
            val mapboxMap = view.getMapboxMap()

            mapboxMap.loadStyleUri(Style.OUTDOORS) { style ->
                val safeFeatureCollection = featureCollection ?: FeatureCollection.fromFeatures(emptyList())

                val source = geoJsonSource("geojson-source") {
                    featureCollection(safeFeatureCollection)
                }
                style.addSource(source)

                val layer = fillLayer("geojson-layer", "geojson-source") {
                    fillColor("rgba(0, 0, 255, 0.3)")
                    fillOutlineColor("rgba(0, 0, 255, 1.0)")
                }
                style.addLayer(layer)

                // Centrar la cámara si hay ubicación del usuario
                userLocation?.let {
                    mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(it)
                            .zoom(12.0)
                            .build()
                    )
                }

                val gesturesPlugin = view.gestures
                gesturesPlugin.addOnMapLongClickListener { point ->
                    onMapLongClick(point)
                    true
                }
                gesturesPlugin.addOnMoveListener(object : OnMoveListener {
                    override fun onMoveBegin(detector: MoveGestureDetector) {
                        hasUserMovedMap.value = true
                    }

                    override fun onMove(detector: MoveGestureDetector): Boolean = false

                    override fun onMoveEnd(detector: MoveGestureDetector) {}
                })

                styleLoaded.value = true
            }
        }
    )

    LaunchedEffect(currentUserLocation, styleLoaded.value) {
        if (styleLoaded.value && currentUserLocation != null && !hasUserMovedMap.value) {
            mapView.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .center(currentUserLocation)
                    .zoom(12.0)
                    .build()
            )
        }
    }

    LaunchedEffect(points, currentSelectedPoint, currentUserLocation, styleLoaded.value) {
        if (!styleLoaded.value) return@LaunchedEffect

        mapView.getMapboxMap().getStyle()?.let {
            annotationManager.deleteAll()
            viewAnnotationManager.removeAllViewAnnotations()

            points.forEach { fav ->
                val point = Point.fromLngLat(fav.longitude, fav.latitude)
                val iconRes = if (fav.type == PointType.ALERT)
                    R.drawable.ic_alert_map_location_icon
                else
                    R.drawable.ic_red_marker

                bitmapFromDrawableRes(context, iconRes)?.let { bmp ->
                    val options = PointAnnotationOptions()
                        .withPoint(point)
                        .withIconImage(bmp)
                    annotationManager.create(options)
                }
            }

            currentUserLocation?.let {
                Timber.tag("MapView").d("User location: $currentUserLocation")
                val bitmap = bitmapFromDrawableRes(context, R.drawable.ic_blue_marker)
                bitmap?.let { bmp ->
                    val options = PointAnnotationOptions()
                        .withPoint(it)
                        .withIconImage(bmp)
                    annotationManager.create(options)
                }
            }
        }
    }

    LaunchedEffect(currentSelectedPoint, styleLoaded.value) {
        if (styleLoaded.value && currentSelectedPoint != null) {
            val mapboxMap = mapView.getMapboxMap()
            val target = Point.fromLngLat(currentSelectedPoint!!.longitude, currentSelectedPoint!!.latitude)
            mapboxMap.easeTo(
                CameraOptions.Builder()
                    .center(target)
                    .zoom(14.0)
                    .build(),
                MapAnimationOptions.mapAnimationOptions {
                    duration(1000L)
                }
            )
        }
    }

    LaunchedEffect(shouldCenterOnUser, currentUserLocation, styleLoaded.value) {
        if (shouldCenterOnUser && styleLoaded.value && currentUserLocation != null) {
            mapView.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .center(currentUserLocation)
                    .zoom(12.0)
                    .build()
            )
            onUserCentered()
        }
    }
}

fun bitmapFromDrawableRes(
    context: Context,
    @DrawableRes resourceId: Int,
    width: Int = 64,
    height: Int = 64
): Bitmap? {
    val drawable = ContextCompat.getDrawable(context, resourceId) ?: return null
    drawable.setBounds(0, 0, width, height)

    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)
    return bitmap
}