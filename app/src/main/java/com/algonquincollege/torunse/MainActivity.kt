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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.algonquincollege.torunse.ui.theme.MyAndroidLabsTheme

//https://developer.android.com/develop/ui/compose/tooling/iterative-development


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //might return null if not on phone
        val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        var sensorListener : SensorEventListener? = null

        setContent {

             var value = remember { mutableStateOf(0.0f) }


            if(sensorListener == null) {
                sensorListener = object : SensorEventListener {

                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

                    }

                    override fun onSensorChanged(event: SensorEvent?) {
                        value.value =
                            event!!.values[0]   //non-null assertion, array of size 1, or size 3
                    }
                }
                //null
                sensorManager.registerListener(sensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
            }

            MyAndroidLabsTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    DisplayLighting(value.value,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
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