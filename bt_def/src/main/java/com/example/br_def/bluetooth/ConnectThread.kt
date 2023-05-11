package com.example.br_def.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import java.io.IOException
import java.util.UUID


class ConnectThread(
    val device: BluetoothDevice,
    private val listener: BluetoothController.Listener,
    val context: Context
) :
    Thread() {
    //private val uuid = "00000000-0000-0000-0000-000000000000"
    //private val uuid = "00001101-0000-1000-8000-00805F9B34FB"
    //private val uuid = "0000110b-0000-1000-8000-00805f9b34fb"

    //private lateinit var bluetoothSocket: BluetoothSocketWrapper
    private var mSocket: BluetoothSocket? = null
    private var fallbackSocket: BluetoothSocket? = null

    init {
        try {
            Log.d("MyLog", device.uuids.toList().toString())
            mSocket =
                device.createRfcommSocketToServiceRecord(UUID.fromString(device.uuids.toList()[0].toString()))
            Log.d("MyLog", "socket created")
        } catch (e: IOException) {
            try {
                val clazz: Class<*> = mSocket?.remoteDevice!!.javaClass
                val paramTypes = arrayOf<Class<*>>(Integer.TYPE)

                val m = clazz.getMethod("createRfcommSocket", *paramTypes)
                val params = arrayOf<Any>(Integer.valueOf(1))

                fallbackSocket = m.invoke(mSocket!!.remoteDevice, params) as BluetoothSocket
                fallbackSocket!!.connect()

                Log.d("MyLog", e.message.toString())
            } catch (ee: IOException) {
                Log.d("MyLog", ee.message.toString())
            }
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
            try {
                mSocket?.close()
            } catch (closeException: IOException) {
                Log.e(
                    "MyLog",
                    "Could not close the client socket on device " + device.address,
                    closeException
                )
            }
            listener.onReceive(BluetoothController.BLUETOOTH_NO_CONNECTED)
            Log.d("MyLog", e.message.toString())
            return
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
                Log.d("MyLog", message)
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

    fun main(message: String) {
        var gatt: BluetoothGatt? = null
        try {
            gatt = device.connectGatt(context, false, UARTBluetoothGattCallback())
        } catch (e: SecurityException) {
            Log.d("MyLog", e.message.toString())
        }

        val uartServiceUUID = UUID.fromString("ab0828b1-198e-4351-b779-901fa0e0371e")
        val uartCharacteristicUUID = UUID.fromString("1a220d0a-6b06-4767-8692-243153d94d85")
        val characteristic: BluetoothGattCharacteristic? = gatt?.getService(uartServiceUUID)
            ?.getCharacteristic(uartCharacteristicUUID)

        try {
            // write data to the characteristic
            val data = message.toByteArray(Charsets.UTF_8)
            characteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            characteristic?.value = data
            gatt?.writeCharacteristic(characteristic)

            // read data from the characteristic
            gatt?.readCharacteristic(characteristic)
            Log.d("MyLog", characteristic?.descriptors.toString())
        } catch (e: SecurityException) {
            Log.d("MyLog", e.message.toString())
        } catch (ex: Exception) {
            Log.d("MyLog", ex.message.toString())
        }
    }
}

class UARTBluetoothGattCallback : BluetoothGattCallback() {
    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        Log.d("MyLog", "onConnectionStateChange")
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        Log.d("MyLog", "onCharacteristicRead")
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        Log.d("MyLog", "onCharacteristicsWrite")
    }
}
