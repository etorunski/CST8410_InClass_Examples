package com.algonquincollege.torunse

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import  android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.algonquincollege.torunse.ui.theme.MyAndroidLabsTheme
import java.util.UUID

//https://developer.android.com/develop/ui/compose/tooling/iterative-development


class MainActivity : ComponentActivity() {

    private var bluetoothManager:BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var gattServer : BluetoothGattServer? = null
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        if(bluetoothManager != null){
            bluetoothAdapter = bluetoothManager?.getAdapter()
        }

        val gattCallbacks = object: BluetoothGattServerCallback() { } //We will implement the inherited functions one at a time and understand what each one does
        val bluetoothLeAdvertiser = bluetoothAdapter?.getBluetoothLeAdvertiser()

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                    isGranted ->
                if (isGranted.values.all { it  == true}) {
                    gattServer = bluetoothManager?.openGattServer(this, gattCallbacks )


                    val settings =  AdvertiseSettings.Builder()
                        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                        .setConnectable(true)
                        .build()

                    val advertisingData = AdvertiseData.Builder()
                        .addServiceUuid(ParcelUuid(UUID.fromString("0000180D-0000-1000-8000-00805F9B34FB")))
                        .setIncludeDeviceName(true)
                        .build()

                    val advertiseCallback = object: AdvertiseCallback() {
                        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                            super.onStartSuccess(settingsInEffect)
                            Log.d(TAG, "Advertising started successfully")
                        }

                        override fun onStartFailure(errorCode: Int) {
                            super.onStartFailure(errorCode)
                            if(errorCode == ADVERTISE_FAILED_INTERNAL_ERROR)
                            Log.e(TAG, "Advertising failed with error code $errorCode")
                        }
                    }

                    bluetoothLeAdvertiser?.startAdvertising(settings, advertisingData, advertiseCallback);

                } else {
                    val i = 0
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }

        setContent {
            MyAndroidLabsTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)){
                        Button(onClick = {


                        },){
                            Text("Click Me")
                        }
                    }
                }
            }
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) //31 or more
        {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE))
        }
        else //30 or less
        {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.BLUETOOTH))
        }

    }

}

@Composable
fun DisplayLighting(lightingValue: Float, modifier: Modifier = Modifier) {

    Column(modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
        Text(
            text = "The lighting is: ${lightingValue}",
            modifier = modifier,
            fontSize = 20.sp,

            )
        Icon(painter=painterResource(R.drawable.beach), contentDescription = "a beach")
        Image(painterResource( R.drawable.beach ),
            contentDescription = "A picture of ??",
            modifier=Modifier.fillMaxWidth(0.5f))
        Button({   } ){
            Text("Click Me")
        }
        Button({   } ){
            Image(painterResource( R.drawable.beach ),
                contentDescription = "A picture of ??",
                modifier=Modifier.fillMaxWidth(0.5f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyAndroidLabsTheme {
        DisplayLighting(45.6f)
    }
}