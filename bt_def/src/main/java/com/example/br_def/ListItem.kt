package com.example.br_def

import android.bluetooth.BluetoothDevice

data class ListItem(
    val device: BluetoothDevice,
    val isChecked: Boolean
)