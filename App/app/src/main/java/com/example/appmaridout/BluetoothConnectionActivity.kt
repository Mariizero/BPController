package com.example.appmaridout

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class BluetoothConnectionActivity: AppCompatActivity() {
    private lateinit var bluetoothSocket: BluetoothSocket

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dois)

        val uuid = intent.getStringExtra("UUID")
        val deviceName = intent.getStringExtra("DeviceName")

        //Executa a função de conexão Bluetooth aqui

        if(uuid != null){
            if(deviceName != null){
                connectToBluetoothDevice(uuid, deviceName)
            }
        }
    }
    // Executa a lógica para conexão Bluetooth
    private fun connectToBluetoothDevice(uuid: String, deviceName: String){
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (ContextCompat.checkSelfPermission(  this@BluetoothConnectionActivity,  Manifest.permission.ACCESS_COARSE_LOCATION  ) !== PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(   this@BluetoothConnectionActivity,   Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(   this@BluetoothConnectionActivity,  arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                    1
                );

            } else {
                ActivityCompat.requestPermissions(    this@BluetoothConnectionActivity, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                    1
                );
            }
        }

        val bondedDevices = bluetoothAdapter.bondedDevices
        var targetDevice: BluetoothDevice? = null

        for (device in bondedDevices){
            if (device.name == deviceName){
                targetDevice = device
            }
        }

        val socket = targetDevice?.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
        socket?.connect()

        bluetoothSocket = socket!!
        BluetoothConnectionHelper.bluetoothSocket = bluetoothSocket
        setResult(Activity.RESULT_OK)
        finish()
    }
    object BluetoothConnectionHelper{
        var bluetoothSocket: BluetoothSocket? = null
    }
}