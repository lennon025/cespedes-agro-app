package com.cespedesagro.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.cespedesagro.app.ui.theme.CespedesColors

@Composable
fun PasswordDialog(
    title: String,
    message: String,
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var password by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CespedesColors.Secundario
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge, color = CespedesColors.Peligro)
                Text(message, color = CespedesColors.TextoMutado)

                OutlinedTextField(
                    value = password,
                    onValueChange = { if (it.length <= 10) password = it },
                    placeholder = { Text("Contraseña", color = CespedesColors.TextoMutado) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CespedesColors.TextoClaro,
                        unfocusedTextColor = CespedesColors.TextoClaro,
                        focusedBorderColor = CespedesColors.Peligro,
                        unfocusedBorderColor = CespedesColors.BordeFuerte,
                        cursorColor = CespedesColors.Peligro,
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
                        text = "Confirmar",
                        onClick = { onSubmit(password) },
                        modifier = Modifier.width(140.dp),
                        enabled = password.isNotBlank()
                    )
                }
            }
        }
    }
}
