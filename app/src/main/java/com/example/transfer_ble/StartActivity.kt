package com.example.transfer_ble

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.transfer_ble.databinding.ContentStartBinding

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ContentStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ContentStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}