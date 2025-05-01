package com.example.technicaltestmaps.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.technicaltestmaps.domain.model.PointType

@Composable
fun AddPointDialog(
    pointName: String,
    pointType: PointType,
    onNameChange: (String) -> Unit,
    onTypeChange: (PointType) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar punto favorito") },
        text = {
            Column {
                OutlinedTextField(
                    value = pointName,
                    onValueChange = onNameChange,
                    label = { Text("Nombre del punto") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Tipo de punto:")
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = pointType == PointType.NORMAL,
                            onClick = { onTypeChange(PointType.NORMAL) }
                        )
                        Text("Normal", modifier = Modifier.padding(end = 16.dp))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = pointType == PointType.ALERT,
                            onClick = { onTypeChange(PointType.ALERT) }
                        )
                        Text("Alerta")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AddPointDialogPreview() {
    var pointName by remember { mutableStateOf("Punto de ejemplo") }
    var pointType by remember { mutableStateOf(PointType.NORMAL) }

    AddPointDialog(
        pointName = pointName,
        pointType = pointType,
        onNameChange = { pointName = it },
        onTypeChange = { pointType = it },
        onDismiss = {},
        onConfirm = {}
    )
}