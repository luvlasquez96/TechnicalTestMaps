package com.example.technicaltestmaps.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.technicaltestmaps.domain.model.FavoritePoint
import com.example.technicaltestmaps.domain.model.PointType
import com.example.technicaltestmaps.R

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    points: List<FavoritePoint>,
    onShowOnMap: (FavoritePoint) -> Unit,
    onDelete: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .padding(16.dp)
    ) {
        items(points) { point ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = point.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Coordenadas (Latitud: %.4f Longitud: %.4f)".format(
                            point.latitude,
                            point.longitude
                        )
                    )
                    Text(text = "Tipo: ${point.type.name}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(onClick = { onShowOnMap(point) }) {
                            Text(stringResource(id=R.string.show_on_map))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = { onDelete(point.id) }) {
                            Text(stringResource(id = R.string.delete))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    val mockPoints = listOf(
        FavoritePoint(1, "Bombo", 0.5833, 32.5333, PointType.NORMAL),
        FavoritePoint(2, "Fort Portal", 0.6710, 30.2750, PointType.ALERT),
        FavoritePoint(3, "Potenza", 40.6420, 15.7990, PointType.NORMAL)
    )

    MaterialTheme {
        FavoritesScreen(
            points = mockPoints,
            onShowOnMap = {},
            onDelete = {}
        )
    }
}
