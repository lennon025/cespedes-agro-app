package com.cespedesagro.app.ui.screens

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cespedesagro.app.data.DeviceType
import com.cespedesagro.app.ui.components.*
import com.cespedesagro.app.ui.theme.CespedesColors
import com.cespedesagro.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: AppViewModel) {
    val connected by vm.btManager.connected.collectAsState()
    val deviceName by vm.btManager.deviceName.collectAsState()
    val deviceType by vm.btManager.deviceType.collectAsState()
    val paired by vm.pairedDevices.collectAsState()
    val uiLang by vm.uiLang.collectAsState()
    val console by vm.console.collectAsState()
    val autoScroll by vm.autoScroll.collectAsState()
    val modoEmergencia by vm.modoEmergencia.collectAsState()

    // Solicitar permisos de Bluetooth (Android 12+)
    val btPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
    } else arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { granted -> if (granted.values.all { it }) vm.refreshPairedDevices() }

    LaunchedEffect(Unit) {
        val ctx = androidx.compose.ui.platform.LocalContext.current
        val allGranted = btPermissions.all { ctx.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
        if (!allGranted) permissionsLauncher.launch(btPermissions) else vm.refreshPairedDevices()
    }

    // Diálogos (Confirmación, Input, Contraseña)
    vm.pendingConfirm.value?.let { req ->
        ConfirmDialog(title = req.title, message = req.message,
            onConfirm = { vm.confirmPending() }, onDismiss = { vm.cancelConfirm() })
    }
    vm.pendingInput.value?.let { req ->
        InputDialog(title = req.title, placeholder = req.placeholder,
            onSubmit = { vm.submitInput(it) }, onDismiss = { vm.cancelInput() })
    }
    vm.pendingPanic.value?.let {
        PasswordDialog(
            title = "🔐 Desbloqueo de Emergencia",
            message = "Ingrese la contraseña para reactivar el sistema.",
            onSubmit = { vm.submitPanicPassword(it) },
            onDismiss = { vm.cancelPanic() }
        )
    }

    Scaffold(
        containerColor = CespedesColors.Primario,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌱 ", style = MaterialTheme.typography.headlineLarge)
                        Text("Céspedes.Agro", style = MaterialTheme.typography.headlineLarge, color = CespedesColors.Acento)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CespedesColors.Secundario)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            // Selector de idioma de la UI
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                listOf("ES", "EN", "RU").forEach { lang ->
                    FilterChip(
                        selected = uiLang == lang,
                        onClick = { vm.setUiLang(lang) },
                        label = { Text(lang) },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CespedesColors.Acento,
                            selectedLabelColor = CespedesColors.Primario
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Estado de conexión Bluetooth
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = CespedesColors.Secundario,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (connected) Icons.Default.BluetoothConnected else Icons.Default.Bluetooth,
                            null,
                            tint = if (connected) CespedesColors.Acento else CespedesColors.Peligro,
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(CespedesColors.FondoInput).padding(8.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                if (connected) "Conectado" else "Desconectado",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (connected) CespedesColors.Acento else CespedesColors.TextoMutado
                            )
                            if (connected) {
                                Text(
                                    "${deviceName ?: ""} ${when (deviceType) {
                                        DeviceType.AGRO -> "• 🌱 Agro"
                                        DeviceType.METEO -> "• 🌦️ Meteo"
                                        else -> ""
                                    }}",
                                    color = CespedesColors.TextoMutado,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    if (connected) {
                        TextButton(onClick = { vm.disconnect() }) {
                            Text("Desconectar", color = CespedesColors.Peligro)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // 🔴 BOTÓN DE PÁNICO (siempre visible si está conectado)
            if (connected) {
                PanicButton(
                    isEmergencyActive = modoEmergencia,
                    onActivate = { vm.activatePanicMode() },
                    onDeactivate = { vm.requestDeactivatePanic() }
                )
                Spacer(Modifier.height(12.dp))
            }

            // Contenido principal: Lista de dispositivos o Pantalla de control
            if (!connected) {
                Text("📡 Dispositivos emparejados", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { vm.refreshPairedDevices() }) {
                        Icon(Icons.Default.Refresh, "Refrescar", tint = CespedesColors.Acento)
                    }
                }
                if (paired.isEmpty()) {
                    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = CespedesColors.Secundario) {
                        Text(
                            "Sin dispositivos emparejados.\nEmpareja tu ESP32 desde los ajustes de Android.",
                            modifier = Modifier.padding(20.dp),
                            color = CespedesColors.TextoMutado
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 260.dp)) {
                        items(paired) { device -> DeviceRow(device) { vm.connect(device) } }
                    }
                }
            } else {
                // Mostrar pantalla según el tipo de dispositivo detectado
                when (deviceType) {
                    DeviceType.AGRO -> AgroScreen(vm)
                    DeviceType.METEO -> MeteoScreen(vm)
                    else -> Text("Dispositivo no reconocido", color = CespedesColors.Aviso)
                }
            }

            Spacer(Modifier.weight(1f))

            // Consola de respuestas (siempre al fondo)
            ConsolePanel(lines = console, autoScroll = autoScroll,
                onClear = { vm.clearConsole() }, onToggleAutoScroll = { vm.toggleAutoScroll() })
        }
    }
}

@Composable
private fun DeviceRow(device: BluetoothDevice, onClick: () -> Unit) {
    val type = DeviceType.fromName(device.name)
    val icon = when (type) {
        DeviceType.AGRO -> "🌱"
        DeviceType.METEO -> "🌦️"
        else -> "📟"
    }
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = CespedesColors.Secundario,
        tonalElevation = 1.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(icon, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(device.name ?: "(sin nombre)", fontWeight = FontWeight.SemiBold, color = CespedesColors.TextoClaro)
                Text(device.address, color = CespedesColors.TextoMutado, style = MaterialTheme.typography.bodySmall)
            }
            Text("→", color = CespedesColors.Acento)
        }
    }
}
