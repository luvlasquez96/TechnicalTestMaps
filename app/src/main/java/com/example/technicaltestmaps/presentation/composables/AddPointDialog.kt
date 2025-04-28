package com.example.technicaltestmaps.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddPointDialog(
    pointName: String,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Nuevo punto favorito") },
        text = {
            Column {
                Text(text = "Escribe el nombre del punto:")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = pointName,
                    onValueChange = onNameChange,
                    placeholder = { Text("Nombre") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm() }, enabled = pointName.isNotBlank()) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}