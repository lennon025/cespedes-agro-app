package com.cespedesagro.app.viewmodel

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cespedesagro.app.data.BluetoothManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppViewModel(app: Application) : AndroidViewModel(app) {

    val btManager = BluetoothManager(app)

    // 🔴 MODO EMERGENCIA
    private val _modoEmergencia = MutableStateFlow(false)
    val modoEmergencia: StateFlow<Boolean> = _modoEmergencia.asStateFlow()

    private val _uiLang = MutableStateFlow("ES")
    val uiLang: StateFlow<String> = _uiLang.asStateFlow()

    data class ConsoleLine(val text: String, val type: LineType, val time: String)
    enum class LineType { SENT, RECEIVED, ERROR, INFO }

    private val _console = MutableStateFlow<List<ConsoleLine>>(emptyList())
    val console: StateFlow<List<ConsoleLine>> = _console.asStateFlow()

    private val _autoScroll = MutableStateFlow(true)
    val autoScroll: StateFlow<Boolean> = _autoScroll.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val pairedDevices: StateFlow<List<BluetoothDevice>> = _pairedDevices.asStateFlow()

    data class ConfirmRequest(val title: String, val message: String, val onConfirm: () -> Unit)
    private val _pendingConfirm = MutableStateFlow<ConfirmRequest?>(null)
    val pendingConfirm: StateFlow<ConfirmRequest?> = _pendingConfirm.asStateFlow()

    data class InputRequest(val title: String, val placeholder: String, val onSubmit: (String) -> Unit)
    private val _pendingInput = MutableStateFlow<InputRequest?>(null)
    val pendingInput: StateFlow<InputRequest?> = _pendingInput.asStateFlow()

    // 🔴 Diálogo de contraseña del botón de pánico
    data class PanicRequest(val mode: PanicMode, val onSubmit: (String) -> Unit)
    enum class PanicMode { ACTIVATE, DEACTIVATE }
    private val _pendingPanic = MutableStateFlow<PanicRequest?>(null)
    val pendingPanic: StateFlow<PanicRequest?> = _pendingPanic.asStateFlow()

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    init {
        viewModelScope.launch {
            btManager.incomingLines.collect { lines ->
                val last = lines.lastOrNull() ?: return@collect
                val existing = _console.value
                if (existing.isEmpty() || existing.last().text != last) {
                    appendLine(last, LineType.RECEIVED)
                }
            }
        }
        refreshPairedDevices()
    }

    fun setUiLang(lang: String) { _uiLang.value = lang }
    fun toggleAutoScroll() { _autoScroll.value = !_autoScroll.value }
    fun refreshPairedDevices() { _pairedDevices.value = btManager.getPairedDevices() }

    fun connect(device: BluetoothDevice) {
        viewModelScope.launch {
            appendLine("Conectando a ${device.name ?: device.address}...", LineType.INFO)
            btManager.connect(device).fold(
                onSuccess = { appendLine("✅ Conectado a ${device.name}", LineType.INFO) },
                onFailure = { appendLine("❌ Error: ${it.message}", LineType.ERROR) }
            )
        }
    }

    fun disconnect() {
        btManager.disconnect()
        _modoEmergencia.value = false
        appendLine("🔌 Desconectado", LineType.INFO)
    }

    fun sendCommand(cmd: String) {
        viewModelScope.launch {
            appendLine("➡ $cmd", LineType.SENT)
            btManager.sendCommand(cmd).onFailure {
                appendLine("❌ Error al enviar: ${it.message}", LineType.ERROR)
            }
        }
    }

    // 🔴 ACTIVAR MODO EMERGENCIA (sin contraseña)
    fun activatePanicMode() {
        sendCommand("OFF")
        _modoEmergencia.value = true
        appendLine("🚨 MODO EMERGENCIA ACTIVADO — Todos los relés apagados", LineType.ERROR)
    }

    // 🔴 DESACTIVAR MODO EMERGENCIA (requiere contraseña 000)
    fun requestDeactivatePanic() {
        _pendingPanic.value = PanicRequest(PanicMode.DEACTIVATE) { password ->
            if (password == "000") {
                sendCommand("ON")
                _modoEmergencia.value = false
                appendLine("✅ MODO EMERGENCIA DESACTIVADO — Sistema operativo", LineType.INFO)
            } else {
                appendLine("❌ Contraseña incorrecta. Use 000", LineType.ERROR)
            }
        }
    }

    fun submitPanicPassword(password: String) {
        _pendingPanic.value?.onSubmit?.invoke(password)
        _pendingPanic.value = null
    }

    fun cancelPanic() { _pendingPanic.value = null }

    fun requestInputFor(prefix: String, title: String, placeholder: String) {
        _pendingInput.value = InputRequest(title, placeholder) { value ->
            sendCommand("$prefix $value")
        }
    }

    fun submitInput(value: String) {
        _pendingInput.value?.onSubmit?.invoke(value)
        _pendingInput.value = null
    }
    fun cancelInput() { _pendingInput.value = null }

    fun requestConfirm(title: String, message: String, onConfirm: () -> Unit) {
        _pendingConfirm.value = ConfirmRequest(title, message, onConfirm)
    }
    fun confirmPending() { _pendingConfirm.value?.onConfirm?.invoke(); _pendingConfirm.value = null }
    fun cancelConfirm() { _pendingConfirm.value = null }

    fun clearConsole() {
        _console.value = emptyList()
        btManager.clearIncoming()
    }

    private fun appendLine(text: String, type: LineType) {
        val line = ConsoleLine(text, type, timeFormat.format(Date()))
        _console.value = (_console.value + line).takeLast(300)
    }
}
