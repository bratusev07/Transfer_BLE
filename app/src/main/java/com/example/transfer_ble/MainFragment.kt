package com.example.transfer_ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.br_def.BluetoothConstants
import com.example.br_def.bluetooth.BluetoothController
import com.example.transfer_ble.databinding.MainFragmentBinding
import java.util.UUID

class MainFragment : Fragment(), BluetoothController.Listener {

    private lateinit var binding: MainFragmentBinding
    private lateinit var bluetoothController: BluetoothController
    private lateinit var bAdapter: BluetoothAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBtAdapter()
        val pref =
            activity?.getSharedPreferences(BluetoothConstants.PREFERENCES, Context.MODE_PRIVATE)
        val mac = pref?.getString(BluetoothConstants.MAC, "")

        bluetoothController = BluetoothController(bAdapter)
        binding.bList.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_deviceListFragment)
        }
        binding.bConnect.setOnClickListener {
            bluetoothController.connect(mac ?: "", this)
        }
        binding.bSend.setOnClickListener {
            bluetoothController.sendMessage("TX: -20")
            bluetoothController.readMessage()
        }
    }

    private fun initBtAdapter() {
        val bManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bAdapter = bManager.adapter
    }

    override fun onReceive(message: String) {
        activity?.runOnUiThread {
            Log.d("MyLog", message)
            when (message) {
                BluetoothController.BLUETOOTH_CONNECTED -> {
                    binding.bConnect.backgroundTintList =
                        AppCompatResources.getColorStateList(requireContext(), R.color.red)
                    binding.bConnect.text = "Disconnect"
                }

                BluetoothController.BLUETOOTH_NO_CONNECTED -> {
                    binding.bConnect.backgroundTintList =
                        AppCompatResources.getColorStateList(requireContext(), R.color.green)
                    binding.bConnect.text = "Connect"
                }

                else -> {
                    binding.tvStatus.text = message
                }
            }
        }
    }
}