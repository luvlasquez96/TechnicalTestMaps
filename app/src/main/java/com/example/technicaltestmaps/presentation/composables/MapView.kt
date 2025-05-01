package com.example.technicaltestmaps.presentation.composables

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.fillLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures

@Composable
fun MapView(
    modifier: Modifier = Modifier,
    points: List<FavoritePoint>,
    userLocation: Point?,
    selectedPoint: FavoritePoint?,
    featureCollection: FeatureCollection?,
    onMapLongClick: (Point) -> Unit
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

    AndroidView(
        modifier = modifier,
        factory = { mapView },
        update = { view ->
            val mapboxMap = view.getMapboxMap()

            mapboxMap.loadStyleUri(Style.MAPBOX_STREETS) { style ->
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

                val target = selectedPoint?.let {
                    Point.fromLngLat(it.longitude, it.latitude)
                } ?: userLocation

                target?.let {
                    mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(it)
                            .zoom(16.0)
                            .build()
                    )
                }

                val gesturesPlugin = view.gestures
                gesturesPlugin.addOnMapLongClickListener { point ->
                    onMapLongClick(point)
                    true
                }
            }
        }
    )

    LaunchedEffect(points, currentSelectedPoint, currentUserLocation) {
        mapView.getMapboxMap().getStyle()?.let {
            annotationManager.deleteAll()
            viewAnnotationManager.removeAllViewAnnotations()

            points.forEach { fav ->
                val point = Point.fromLngLat(fav.longitude, fav.latitude)

                if (fav.type == PointType.ALERT) {
                    val bitmap = bitmapFromDrawableRes(
                        context,
                        R.drawable.ic_alert_map_location_icon
                    )
                    bitmap?.let { bmp ->
                        val options = PointAnnotationOptions()
                            .withPoint(point)
                            .withIconImage(bmp)
                        annotationManager.create(options)
                    }
                } else {
                    val bitmap = bitmapFromDrawableRes(
                        context,
                        R.drawable.ic_red_marker
                    )
                    bitmap?.let { bmp ->
                        val options = PointAnnotationOptions()
                            .withPoint(point)
                            .withIconImage(bmp)
                        annotationManager.create(options)
                    }
                }
            }

            currentUserLocation?.let {
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
