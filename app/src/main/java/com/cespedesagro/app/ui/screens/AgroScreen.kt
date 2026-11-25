package com.cespedesagro.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cespedesagro.app.ui.components.CommandButton
import com.cespedesagro.app.ui.theme.CespedesColors
import com.cespedesagro.app.viewmodel.AppViewModel

@Composable
fun AgroScreen(vm: AppViewModel) {
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

        SectionCard("🚰 Relés R1-R6 (Válvulas)") {
            listOf("R1", "R2", "R3", "R4", "R5", "R6").forEach { r ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CommandButton(text = "🔓 $r ON", onClick = { vm.sendCommand("$r 1") },
                        modifier = Modifier.weight(1f), enabled = !actuadoresBloqueados)
                    CommandButton(text = "🔒 $r OFF", onClick = { vm.sendCommand("$r 0") },
                        modifier = Modifier.weight(1f), enabled = !actuadoresBloqueados)
                }
                Spacer(Modifier.height(6.dp))
            }
        }

        SectionCard("⚡ Relés L1 y C1") {
            listOf("L1", "C1").forEach { r ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CommandButton(text = "⚡ $r ON", onClick = { vm.sendCommand("$r 1") },
                        modifier = Modifier.weight(1f), enabled = !actuadoresBloqueados)
                    CommandButton(text = "⚫ $r OFF", onClick = { vm.sendCommand("$r 0") },
                        modifier = Modifier.weight(1f), enabled = !actuadoresBloqueados)
                }
                Spacer(Modifier.height(6.dp))
            }
        }

        ConfigSection(vm, connected)
    }
}

@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CespedesColors.Secundario,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = CespedesColors.Acento)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun ConfigSection(vm: AppViewModel, connected: Boolean) {
    SectionCard("⚙️ Configuración") {
        CommandButton(text = "📡 Conectar WIFI", onClick = {
            vm.requestInputFor("WIFI", "SSID|Contraseña", "MiRed|miClave123")
        }, enabled = connected)
        Spacer(Modifier.height(8.dp))
        CommandButton(text = "🌐 Enviar MQTT", onClick = {
            vm.requestInputFor("MQTT", "IP del servidor MQTT", "192.168.1.100")
        }, enabled = connected)
        Spacer(Modifier.height(8.dp))
        CommandButton(text = "🆔 Enviar ID", onClick = {
            vm.requestInputFor("ID", "Nuevo ID de área", "P001")
        }, enabled = connected)
        Spacer(Modifier.height(8.dp))
        CommandButton(text = "🗑️ BORRAR CONFIG", onClick = {
            vm.requestConfirm("¿Borrar configuración?", "Se borrarán WiFi, MQTT e ID.") {
                vm.sendCommand("BORRAR")
            }
        }, enabled = connected, danger = true)
        Spacer(Modifier.height(8.dp))
        CommandButton(text = "🔄 REINICIAR", onClick = {
            vm.requestConfirm("¿Reiniciar dispositivo?", "El ESP32 se reiniciará ahora.") {
                vm.sendCommand("REINICIAR")
            }
        }, enabled = connected, danger = true)
    }
}
