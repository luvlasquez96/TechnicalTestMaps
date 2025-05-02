package com.example.technicaltestmaps.presentation.composables

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
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
import com.mapbox.bindgen.Value
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.circleLayer
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

    val pointAnnotationManager = remember(mapView) {
        mapView.annotations.createPointAnnotationManager()
    }

    val currentSelectedPoint by rememberUpdatedState(selectedPoint)

    val styleLoaded = remember { mutableStateOf(false) }
    val hasUserMovedMap = remember { mutableStateOf(false) }

    val alertAnimator = remember {
        ValueAnimator.ofFloat(4f, 12f).apply {
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { mapView },
        update = { view ->
            val mapboxMap = view.getMapboxMap()
            mapboxMap.loadStyleUri(Style.OUTDOORS) { style ->
                val safeFeatureCollection =
                    featureCollection ?: FeatureCollection.fromFeatures(emptyList())

                val source = geoJsonSource("geojson-source") {
                    featureCollection(safeFeatureCollection)
                }
                style.addSource(source)

                val layer = fillLayer("geojson-layer", "geojson-source") {
                    fillColor("rgba(0, 0, 255, 0.3)")
                    fillOutlineColor("rgba(0, 0, 255, 1.0)")
                }
                style.addLayer(layer)

                val alertPoints = points.filter { it.type == PointType.ALERT }
                val alertFeatures = alertPoints.map {
                    Feature.fromGeometry(Point.fromLngLat(it.longitude, it.latitude))
                }
                val alertSource = geoJsonSource("alert-source") {
                    featureCollection(FeatureCollection.fromFeatures(alertFeatures))
                }
                style.addSource(alertSource)

                val alertLayer = circleLayer("alert-layer", "alert-source") {
                    circleColor("#FF0000")
                    circleRadius(8.0)
                    circleOpacity(0.6)
                    circleStrokeWidth(1.0)
                    circleStrokeColor("#880000")
                }
                style.addLayer(alertLayer)

                alertAnimator.addUpdateListener {
                    val radius = it.animatedValue as Float
                    mapView.getMapboxMap().getStyle()?.setStyleLayerProperty(
                        "alert-layer",
                        "circle-radius",
                        Value(radius.toDouble())
                    )
                }

                alertAnimator.start()

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

    LaunchedEffect(points, userLocation, styleLoaded.value) {
        if (!styleLoaded.value) return@LaunchedEffect

        pointAnnotationManager.deleteAll()

        userLocation?.let { location ->
            val bitmap = bitmapFromDrawableRes(context, R.drawable.ic_blue_marker)
            bitmap?.let { bmp ->
                val options = PointAnnotationOptions()
                    .withPoint(location)
                    .withIconImage(bmp)
                pointAnnotationManager.create(options)
            }
        }

        points.filter { it.type != PointType.ALERT }.forEach { fav ->
            val point = Point.fromLngLat(fav.longitude, fav.latitude)
            val iconRes = R.drawable.ic_red_marker
            bitmapFromDrawableRes(context, iconRes)?.let { bmp ->
                val options = PointAnnotationOptions()
                    .withPoint(point)
                    .withIconImage(bmp)
                pointAnnotationManager.create(options)
            }
        }
    }

    LaunchedEffect(userLocation, styleLoaded.value) {
        if (styleLoaded.value && userLocation != null && !hasUserMovedMap.value) {
            mapView.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .center(userLocation)
                    .zoom(12.0)
                    .build()
            )
        }
    }

    LaunchedEffect(currentSelectedPoint, styleLoaded.value) {
        if (styleLoaded.value && currentSelectedPoint != null) {
            val mapboxMap = mapView.getMapboxMap()
            val target =
                Point.fromLngLat(currentSelectedPoint!!.longitude, currentSelectedPoint!!.latitude)
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

    LaunchedEffect(shouldCenterOnUser, userLocation, styleLoaded.value) {
        if (shouldCenterOnUser && styleLoaded.value && userLocation != null) {
            mapView.getMapboxMap().easeTo(
                CameraOptions.Builder()
                    .center(userLocation)
                    .zoom(12.0)
                    .build(),
                MapAnimationOptions.mapAnimationOptions {
                    duration(1000L)
                }
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