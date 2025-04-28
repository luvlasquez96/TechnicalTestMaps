package com.example.technicaltestmaps.presentation.composables

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.example.technicaltestmaps.R
import com.example.technicaltestmaps.domain.model.FavoritePoint
import com.example.technicaltestmaps.domain.model.PointType
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
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
    onMapLongClick: (Point) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    AndroidView(
        modifier = modifier,
        factory = { mapView },
        update = { view ->
            val mapboxMap = view.getMapboxMap()

            mapboxMap.loadStyleUri(Style.MAPBOX_STREETS) {
                val annotationApi = view.annotations
                val pointAnnotationManager = annotationApi.createPointAnnotationManager()
                pointAnnotationManager.deleteAll()

                userLocation?.let {
                    val bitmap = bitmapFromDrawableRes(context, R.drawable.ic_blue_marker)
                    bitmap?.let { bmp ->
                        val options = PointAnnotationOptions()
                            .withPoint(it)
                            .withIconImage(bmp)
                        pointAnnotationManager.create(options)
                    }
                }

                points.forEach { fav ->
                    val iconRes = when (fav.type) {
                        PointType.ALERT -> R.drawable.ic_alert_map_location_icon
                        PointType.NORMAL -> R.drawable.ic_red_marker
                    }
                    val bitmap = bitmapFromDrawableRes(context, iconRes)
                    bitmap?.let { bmp ->
                        val point = Point.fromLngLat(fav.longitude, fav.latitude)
                        val options = PointAnnotationOptions()
                            .withPoint(point)
                            .withIconImage(bmp)
                        pointAnnotationManager.create(options)
                    }
                }
            }

            val target = selectedPoint?.let {
                Point.fromLngLat(it.longitude, it.latitude)
            } ?: userLocation

            target?.let {
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
        }
    )
}

fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(context, resourceId) ?: return null
    val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}
