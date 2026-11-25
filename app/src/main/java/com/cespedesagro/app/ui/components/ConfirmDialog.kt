package com.cespedesagro.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cespedesagro.app.ui.theme.CespedesColors

/**
 * Diálogo de confirmación para acciones destructivas (BORRAR, REINICIAR).
 * / Диалог подтверждения для деструктивных действий.
 * / Confirmation dialog for destructive actions.
 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CespedesColors.Secundario,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(message, color = CespedesColors.TextoMutado)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = CespedesColors.TextoMutado)
                    }
                    Spacer(Modifier.width(8.dp))
                    CommandButton(
                        text = "Confirmar",
                        onClick = onConfirm,
                        modifier = Modifier.width(140.dp),
                        danger = true
                    )
                }
            }
        }
    }
}

/**
 * Diálogo de entrada de texto (para WIFI, MQTT, ID).
 * / Диалог ввода текста (для WIFI, MQTT, ID).
 * / Text input dialog (for WIFI, MQTT, ID).
 */
@Composable
fun InputDialog(
    title: String,
    placeholder: String,
    initialValue: String = "",
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var value by remember { mutableStateOf(initialValue) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CespedesColors.Secundario
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    placeholder = { Text(placeholder, color = CespedesColors.TextoMutado) },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CespedesColors.TextoClaro,
                        unfocusedTextColor = CespedesColors.TextoClaro,
                        focusedBorderColor = CespedesColors.Acento,
                        unfocusedBorderColor = CespedesColors.BordeFuerte,
                        cursorColor = CespedesColors.Acento,
                        focusedContainerColor = CespedesColors.FondoInput,
                        unfocusedContainerColor = CespedesColors.FondoInput
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = CespedesColors.TextoMutado)
                    }
                    Spacer(Modifier.width(8.dp))
                    CommandButton(
                        text = "Enviar",
                        onClick = { if (value.isNotBlank()) onSubmit(value) },
                        modifier = Modifier.width(140.dp),
                        enabled = value.isNotBlank()
                    )
                }
            }
        }
    }
}
