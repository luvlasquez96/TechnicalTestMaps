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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
                Row {
                    RadioButton(
                        selected = pointType == PointType.NORMAL,
                        onClick = { onTypeChange(PointType.NORMAL) }
                    )
                    Text("Normal", modifier = Modifier.padding(end = 16.dp))
                    RadioButton(
                        selected = pointType == PointType.ALERT,
                        onClick = { onTypeChange(PointType.ALERT) }
                    )
                    Text("Alerta")
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