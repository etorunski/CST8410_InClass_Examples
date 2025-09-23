package com.algonquincollege.torunse

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import  android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.algonquincollege.torunse.ui.theme.MyAndroidLabsTheme

//https://developer.android.com/develop/ui/compose/tooling/iterative-development


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAndroidLabsTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    DisplayLighting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun DisplayLighting( modifier: Modifier = Modifier) {

    var sensorManager = LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    var value = remember{  mutableStateOf( 0.0f )  }//makes it not garbage collected


    //might return null if not on phone
    val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    val sensorListener = object: SensorEventListener{

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }
        override fun onSensorChanged(event: SensorEvent?) {
            value.value = event!!.values[0]   //non-null assertion, array of size 1, or size 3
        }
    }
                                                //null
    sensorManager.registerListener(sensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)

    Text(
        text = "The lighting is: ${value.value}",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyAndroidLabsTheme {
        DisplayLighting()
    }
}