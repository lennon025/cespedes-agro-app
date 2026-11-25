package com.cespedesagro.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cespedesagro.app.ui.theme.CespedesColors

/**
 * Botón de pánico grande.
 * - Si el sistema NO está en emergencia → muestra "🚨 EMERGENCIA" (rojo)
 * - Si el sistema ESTÁ en emergencia → muestra "🔓 DESBLOQUEAR" (amarillo)
 */
@Composable
fun PanicButton(
    isEmergencyActive: Boolean,
    onActivate: () -> Unit,
    onDeactivate: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isEmergencyActive) {
        // Modo DESBLOQUEAR (Amarillo)
        Button(
            onClick = onDeactivate,
            modifier = modifier
                .fillMaxWidth()
                .height(90.dp)
                .border(3.dp, CespedesColors.Aviso, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CespedesColors.Aviso,
                contentColor = CespedesColors.Primario
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔓 DESBLOQUEAR", fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("Requiere contraseña 000", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    } else {
        // Modo ACTIVAR EMERGENCIA (Rojo)
        Button(
            onClick = onActivate,
            modifier = modifier
                .fillMaxWidth()
                .height(90.dp)
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CespedesColors.Peligro,
                contentColor = Color.White
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🚨 EMERGENCIA", fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("Apaga TODOS los relés", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
