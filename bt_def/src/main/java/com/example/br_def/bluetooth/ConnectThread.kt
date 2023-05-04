package com.example.br_def.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID


class ConnectThread(device: BluetoothDevice, private val listener: BluetoothController.Listener) :
    Thread() {
    private val uuid = "ab0828b1-198e-4351-b779-901fa0e0371e"
    //private lateinit var bluetoothSocket: BluetoothSocketWrapper
    private var mSocket: BluetoothSocket? = null

    init {
        try {
            mSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
            Log.d("MyLog", "socket created")
        } catch (e: IOException) {
            Log.d("MyLog", e.message.toString())
        } catch (se: SecurityException) {
            Log.d("MyLog", se.message.toString())
        }
    }

    override fun run() {
        try {
            mSocket?.connect()
            Log.d("MyLog", "socket connected")
            listener.onReceive(BluetoothController.BLUETOOTH_CONNECTED)
        } catch (e: IOException) {
            listener.onReceive(BluetoothController.BLUETOOTH_NO_CONNECTED)
            Log.d("MyLog", e.message.toString())
        } catch (se: SecurityException) {
            Log.d("MyLog", se.message.toString())
        }
    }

    /*private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Standard SerialPortService ID

    fun sendMessageTest(message: String) {
        var socket: BluetoothSocket? = null

        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
            socket.connect()
        } catch (e: IOException) {
            Log.d("MyLog", e.message.toString())
        } catch (se: SecurityException){
            Log.d("MyLog", se.message.toString())
        }

        if (socket == null) {
            Log.d("MyLog", "socket is null")
            return
        }

        val outputStream = socket.outputStream
        val buffer = message.toByteArray()
        try {
            outputStream.write(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }*/
    fun sendMessage(message: String) {
        try {
            mSocket?.outputStream?.write(message.toByteArray())
        } catch (e: IOException) {
            listener.onReceive(BluetoothController.BLUETOOTH_NO_CONNECTED)
        }
    }

    fun readMessage() {
        val buffer = ByteArray(10)
        while (true) {
            try {
                val length = mSocket?.inputStream?.read(buffer)
                val message = String(buffer, 0, length ?: 0)
                listener.onReceive(message)
            } catch (e: IOException) {
                Log.d("MyLog", e.message.toString())
                break
            }
        }
    }

    fun closeConnection() {
        try {
            mSocket?.close()
        } catch (e: IOException) {

        }
    }
}