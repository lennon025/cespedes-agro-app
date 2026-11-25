package com.cespedesagro.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cespedesagro.app.ui.theme.CespedesColors
import com.cespedesagro.app.viewmodel.AppViewModel

@Composable
fun ConsolePanel(
    lines: List<AppViewModel.ConsoleLine>,
    autoScroll: Boolean,
    onClear: () -> Unit,
    onToggleAutoScroll: () -> Unit
) {
    val listState = rememberLazyListState()

    // Auto-scroll cuando llegan líneas nuevas
    LaunchedEffect(lines.size, autoScroll) {
        if (autoScroll && lines.isNotEmpty()) listState.animateScrollToItem(lines.size - 1)
    }

    Surface(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = CespedesColors.Primario,
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Cabecera de la consola
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📟 Consola", style = MaterialTheme.typography.titleMedium, color = CespedesColors.Acento)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onToggleAutoScroll) {
                        Text(if (autoScroll) "⏸ Auto" else "▶ Auto", color = CespedesColors.TextoMutado)
                    }
                    IconButton(onClick = onClear) {
                        Icon(Icons.Default.DeleteSweep, "Limpiar", tint = CespedesColors.TextoMutado)
                    }
                }
            }

            // Líneas de la consola
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
                    .background(CespedesColors.FondoInput, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                itemsIndexed(lines) { _, line ->
                    val color = when (line.type) {
                        AppViewModel.LineType.SENT -> CespedesColors.Acento
                        AppViewModel.LineType.RECEIVED -> CespedesColors.TextoClaro
                        AppViewModel.LineType.ERROR -> CespedesColors.Peligro
                        AppViewModel.LineType.INFO -> CespedesColors.Aviso
                    }
                    Text(
                        text = "[${line.time}] ${line.text}",
                        color = color,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
