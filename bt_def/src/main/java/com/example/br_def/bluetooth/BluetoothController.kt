package com.example.br_def.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.util.Log

class BluetoothController(private val adapter: BluetoothAdapter, val context: Context) {
    private var connectThread: ConnectThread? = null

    fun connect(mac: String, listener: Listener) {
        if (adapter.isEnabled && mac.isNotEmpty()) {
            try {
                adapter.cancelDiscovery()
            } catch (se: SecurityException) {
                Log.d("MyLog", se.message.toString())
            }
            val device = adapter.getRemoteDevice(mac)
            connectThread = ConnectThread(device, listener, context)
            connectThread?.start()
        }
    }

    fun sendMessage(message: String) {
        connectThread?.sendMessage(message)
    }

    fun readMessage() {
        connectThread?.readMessage()
    }

    fun sendMsg(message: String) {
        connectThread?.main(message)
    }

    fun closeConnection() {
        connectThread?.closeConnection()
    }

    companion object {
        const val BLUETOOTH_CONNECTED = "bluetooth_connected"
        const val BLUETOOTH_NO_CONNECTED = "bluetooth_no_connected"
    }

    interface Listener {
        fun onReceive(message: String)
    }
}