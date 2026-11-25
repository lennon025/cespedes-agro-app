package com.cespedesagro.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cespedesagro.app.ui.components.CommandButton
import com.cespedesagro.app.viewmodel.AppViewModel

@Composable
fun MeteoScreen(vm: AppViewModel) {
    val connected by vm.btManager.connected.collectAsState()
    val modoEmergencia by vm.modoEmergencia.collectAsState()
    // Bloquear actuadores si no hay conexión o si está en modo emergencia
    val actuadoresBloqueados = !connected || modoEmergencia

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionCard("📊 Estado") {
            CommandButton(text = "📊 ESTADO", onClick = { vm.sendCommand("ESTADO") }, enabled = connected)
            Spacer(Modifier.height(8.dp))
            CommandButton(text = "🌡️ SENSORES", onClick = { vm.sendCommand("SENSORES") }, enabled = connected)
            Spacer(Modifier.height(8.dp))
            CommandButton(text = "🌐 IP", onClick = { vm.sendCommand("IP") }, enabled = connected)
        }

        SectionCard("⚡ Actuadores Meteo") {
            // Interruptor
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CommandButton(text = "⚡ Interruptor ON", onClick = { vm.sendCommand("Interruptor 1") },
                    modifier = Modifier.weight(1f), enabled = !actuadoresBloqueados)
                CommandButton(text = "⚫ Interruptor OFF", onClick = { vm.sendCommand("Interruptor 0") },
                    modifier = Modifier.weight(1f), enabled = !actuadoresBloqueados)
            }
            Spacer(Modifier.height(8.dp))
            // Contactor Principal
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CommandButton(text = "🔓 Contactor ON", onClick = { vm.sendCommand("Contactor 1") },
                    modifier = Modifier.weight(1f), enabled = !actuadoresBloqueados)
                CommandButton(text = "🔒 Contactor OFF", onClick = { vm.sendCommand("Contactor 0") },
                    modifier = Modifier.weight(1f), enabled = !actuadoresBloqueados)
            }
        }

        ConfigSection(vm, connected)
    }
}
