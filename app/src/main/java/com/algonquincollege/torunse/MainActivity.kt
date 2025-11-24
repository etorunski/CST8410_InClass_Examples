package com.algonquincollege.torunse

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.algonquincollege.torunse.ui.theme.MyAndroidLabsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

//https://developer.android.com/develop/ui/compose/tooling/iterative-development


class MainActivity : ComponentActivity() {

    private var device : BluetoothDevice? = null

    /////////// Step 1
    private var bluetoothManager:BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var gattServer : BluetoothGattServer? = null
    ////////////// End of Step 1

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //  Step 1
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        if(bluetoothManager != null){
            bluetoothAdapter = bluetoothManager?.getAdapter()
        }

        ///////// end of step 1


        //This is an object that implements the various functions that are called during a bluetooth connection session:
        val gattCallbacks = object: BluetoothGattServerCallback() {

            //Step 7, a device is trying to connect, or is disconnecting:
            override fun onConnectionStateChange(
                d: BluetoothDevice?,
                status: Int,
                newState: Int
            ) {
                super.onConnectionStateChange(d, status, newState)
                when(newState)
                {

                    BluetoothGatt.STATE_CONNECTED -> {
                        device = d
                        Log.d(TAG, "Connected to $d")

                    }
                    BluetoothGatt.STATE_DISCONNECTED -> {
                        device = null
                        Log.d(TAG, "Disconnected from $d")}
                }
            }
            /////////////// end of Step 7


            override fun onCharacteristicReadRequest(
                device: BluetoothDevice?,
                requestId: Int,
                offset: Int,
                characteristic: BluetoothGattCharacteristic?
            ) {
                super.onCharacteristicReadRequest(device, requestId, offset, characteristic)

                //a client is asking for the current value of the charactistic (variable)
                gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, characteristic!!.value)
            }

            //step 12a:
            override fun onCharacteristicWriteRequest(
                d: BluetoothDevice?,
                requestId: Int,
                characteristic: BluetoothGattCharacteristic?,
                preparedWrite: Boolean,
                responseNeeded: Boolean,
                offset: Int,
                value: ByteArray?
            ) {
                super.onCharacteristicWriteRequest(
                    d,
                    requestId,
                    characteristic,
                    preparedWrite,
                    responseNeeded,
                    offset,
                    value
                )
                //get the new value from the client:
                val str = String(value!!)
                //write the new value to the characteristic:
                characteristic?.setValue(str)

                Log.d(TAG, "write request: ${str}")
                //if the server needs to acknowledge the request:
                if(responseNeeded)
                    gattServer?.sendResponse(d, requestId, BluetoothGatt.GATT_SUCCESS, 0, characteristic!!.value)

                ///////////// end of step 12


                //step 13, if the server wants to change the value, do it and then notify clients of the change
                CoroutineScope(Dispatchers.IO).launch {
                    Thread.sleep(3000)
                    //set the server's characteristic value
                   characteristic?.setValue("Got your message")





                    //notify clients of the change
                    gattServer?.notifyCharacteristicChanged(device, characteristic, true)

                    //Step 14a:
                    //     gattServer?.cancelConnection(device)
                    //end of 14a
                }
                ////////////// end of step 13
            }

            override fun onServiceAdded(
                status: Int,
                service: BluetoothGattService?
            ) {
                super.onServiceAdded(status, service)
                if(status == BluetoothGatt.GATT_SUCCESS)
                {
                    Log.d(TAG, "onServiceAdded success")
                }
                else
                    Log.d(TAG, "onServiceAdded failed with status: $status")
            }
        } //We will implement the inherited functions one at a time and understand what each one does
        val bluetoothLeAdvertiser = bluetoothAdapter?.getBluetoothLeAdvertiser()


        // Step 2a
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                    isGranted ->
                if (isGranted.values.all { it  == true}) {

                    // Step 2
                    gattServer = bluetoothManager?.openGattServer(this, gattCallbacks )
                    ///////////// end of step 2


                                        //to show a String UUID: ParcelUuid(UUID.randomUUID()).toString()
                    //Step 10:
                    val serviceUUID = UUID.fromString("0000180D-0000-1000-8000-00805F9B34FB")
                    val service = BluetoothGattService(serviceUUID,
                        BluetoothGattService.SERVICE_TYPE_PRIMARY )

                    val myCharacteristic =
                        BluetoothGattCharacteristic(serviceUUID,
                            BluetoothGattCharacteristic.PROPERTY_NOTIFY or
                                    BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_WRITE,
                            BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE)
                    myCharacteristic.setValue("Hello world!".toByteArray(Charsets.UTF_8))
                    service?.addCharacteristic(myCharacteristic)
                    ///////////////// end of Step 10

                    gattServer?.addService(service) //this will call onServiceAdded() callback

                    // Step 3
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
                            Log.e(TAG, "Advertising failed with error code $errorCode")
                        }
                    }

                    bluetoothLeAdvertiser?.startAdvertising(settings, advertisingData, advertiseCallback);
                    //////// end of step 3
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
            var serverString = remember{mutableStateOf("Start Server")}
            var clientString = remember{mutableStateOf("Start Client")}
            var stringUUID = remember{ mutableStateOf("") }
            MyAndroidLabsTheme {
                Scaffold( modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomAppBar(
                            modifier = Modifier.fillMaxWidth(),
                            actions = {
                                TextButton(onClick = { /* do something */ })
                                {
                                    Text(serverString.value)
                                }
                                Spacer(modifier = Modifier.weight(1f, true))
                                TextButton(onClick = { /* do something */ }) {
                                    Text(clientString.value)
                                }
                            },
                        )
                    },

                    ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)){
                        Button(onClick = {
                            //create a random UUID and parcel it to a string
                            stringUUID.value = ParcelUuid(UUID.randomUUID()).toString()

                        }){ Text("Click Me") }

                        if(stringUUID.value.isNotEmpty())
                        {
                            AsyncImage(model="https://api.qrserver.com/v1/create-qr-code/?data=${stringUUID.value}&size=300x300", contentDescription = "A QR code")
                        }
                    }
                }
            }
        }

        //Step 2a  //not waiting for a button click the way this is written
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) //31 or more
        {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE))
        }
        else //30 or less
        {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.BLUETOOTH))
        }
        ///////////////// end of step 2a
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