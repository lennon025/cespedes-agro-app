package com.cespedesagro.app.data

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class BluetoothManager(private val context: Context) {

    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val btManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val adapter: BluetoothAdapter? = btManager.adapter

    private val _connected = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected.asStateFlow()

    private val _deviceName = MutableStateFlow<String?>(null)
    val deviceName: StateFlow<String?> = _deviceName.asStateFlow()

    private val _deviceType = MutableStateFlow(DeviceType.UNKNOWN)
    val deviceType: StateFlow<DeviceType> = _deviceType.asStateFlow()

    private val _incomingLines = MutableStateFlow<List<String>>(emptyList())
    val incomingLines: StateFlow<List<String>> = _incomingLines.asStateFlow()

    private var socket: BluetoothSocket? = null
    private var out: OutputStream? = null
    private var inp: InputStream? = null
    private var readThread: Thread? = null

    fun getPairedDevices(): List<BluetoothDevice> = adapter?.bondedDevices?.toList() ?: emptyList()

    suspend fun connect(device: BluetoothDevice): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            disconnect()
            socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            socket?.connect()
            out = socket?.outputStream
            inp = socket?.inputStream
            _connected.value = true
            _deviceName.value = device.name ?: device.address
            _deviceType.value = DeviceType.fromName(device.name)
            startReading()
            Result.success(Unit)
        } catch (e: Exception) {
            _connected.value = false
            Result.failure(e)
        }
    }

    fun disconnect() {
        try {
            readThread?.interrupt(); readThread = null
            inp?.close(); out?.close(); socket?.close()
        } catch (_: IOException) {}
        socket = null; inp = null; out = null
        _connected.value = false
        _deviceName.value = null
        _deviceType.value = DeviceType.UNKNOWN
    }

    suspend fun sendCommand(cmd: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (!_connected.value) return@withContext Result.failure(IOException("Sin conexión"))
        try {
            val payload = if (cmd.endsWith("\n")) cmd else "$cmd\n"
            out?.write(payload.toByteArray())
            out?.flush()
            Result.success(Unit)
        } catch (e: IOException) { Result.failure(e) }
    }

    private fun startReading() {
        readThread = Thread {
            val buf = StringBuilder()
            try {
                val stream = inp ?: return@Thread
                val data = ByteArray(1024)
                while (!Thread.currentThread().isInterrupted) {
                    val n = stream.read(data)
                    if (n <= 0) break
                    for (ch in String(data, 0, n)) {
                        if (ch == '\n' || ch == '\r') {
                            val line = buf.toString().trim()
                            if (line.isNotEmpty()) {
                                val cur = _incomingLines.value.toMutableList()
                                cur.add(line)
                                if (cur.size > 200) cur.removeAt(0)
                                _incomingLines.value = cur
                            }
                            buf.clear()
                        } else buf.append(ch)
                    }
                }
            } catch (_: IOException) {}
            finally { _connected.value = false }
        }.apply { isDaemon = true; start() }
    }

    fun clearIncoming() { _incomingLines.value = emptyList() }
}
