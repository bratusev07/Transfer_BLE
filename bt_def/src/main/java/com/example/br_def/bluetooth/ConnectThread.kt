package com.example.br_def.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID


class ConnectThread(device: BluetoothDevice, val listener: BluetoothController.Listener) :
    Thread() {
    private val uuid = "ab0828b1-198e-4351-b779-901fa0e0371e"
    //private lateinit var bluetoothSocket: BluetoothSocketWrapper
    private var mSocket: BluetoothSocket? = null

    init {
        try {
            mSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
        } catch (e: IOException) {

        } catch (se: SecurityException) {
            Log.d("MyLog", se.message.toString())

        }
    }

    override fun run() {
        try {
            mSocket?.connect()
            listener.onReceive(BluetoothController.BLUETOOTH_CONNECTED)
        } catch (e: IOException) {
            listener.onReceive(BluetoothController.BLUETOOTH_NO_CONNECTED)
            Log.d("MyLog", e.message.toString())

            //bluetoothSocket = FallbackBluetoothSocket(bluetoothSocket.underlyingSocket!!)
        } catch (se: SecurityException) {
            Log.d("MyLog", se.message.toString())
        }
    }

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


   /* interface BluetoothSocketWrapper {
        @get:Throws(IOException::class)
        val inputStream: InputStream?

        @get:Throws(IOException::class)
        val outputStream: OutputStream?
        val remoteDeviceName: String?

        @Throws(IOException::class)
        fun connect()
        val remoteDeviceAddress: String?

        @Throws(IOException::class)
        fun close()
        val underlyingSocket: BluetoothSocket?
    }

    open class NativeBluetoothSocket(override val underlyingSocket: BluetoothSocket) :
        BluetoothSocketWrapper {

        @get:Throws(IOException::class)
        override val inputStream: InputStream?
            get() = underlyingSocket.inputStream

        @get:Throws(IOException::class)
        override val outputStream: OutputStream?
            get() = underlyingSocket.outputStream
        override val remoteDeviceName: String?
            get() = try {
                underlyingSocket.remoteDevice.name
            }catch (e:SecurityException){
                Log.d("MyLog", e.message.toString())
                "unnamed"
            }

        @Throws(IOException::class)
        override fun connect() {
            try {
                underlyingSocket.connect()
            }catch (e: SecurityException){
                Log.d("MyLog", e.message.toString())
            }
        }

        override val remoteDeviceAddress: String?
            get() = underlyingSocket.remoteDevice.address

        @Throws(IOException::class)
        override fun close() {
            underlyingSocket.close()
        }
    }

    class FallbackBluetoothSocket(tmp: BluetoothSocket) : NativeBluetoothSocket(tmp) {
        private var fallbackSocket: BluetoothSocket? = null

        init {
            fallbackSocket = try {
                val clazz: Class<*> = tmp.remoteDevice.javaClass
                val paramTypes = arrayOf<Class<*>>(Integer.TYPE)
                val m = clazz.getMethod("createRfcommSocket", *paramTypes)
                val params = arrayOf<Any>(Integer.valueOf(1))
                m.invoke(tmp.remoteDevice, *params) as BluetoothSocket
            } catch (e: Exception) {
                throw FallbackException(e)
            }
        }

        @get:Throws(IOException::class)
        override val inputStream: InputStream?
            get() = fallbackSocket!!.inputStream

        @get:Throws(IOException::class)
        override val outputStream: OutputStream?
            get() = fallbackSocket!!.outputStream

        @Throws(IOException::class)
        override fun connect() {
            try {
                fallbackSocket!!.connect()
            }catch (e: SecurityException){
                Log.d("MyLog", e.message.toString())
            }
        }

        @Throws(IOException::class)
        override fun close() {
            fallbackSocket!!.close()
        }
    }

    class FallbackException(e: Exception?) : Exception(e) {
        companion object {
            /**
             *
             */
            private const val serialVersionUID = 1L
        }
    }*/
}