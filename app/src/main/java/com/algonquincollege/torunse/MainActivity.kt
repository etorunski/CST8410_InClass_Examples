package com.algonquincollege.torunse

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import  android.hardware.SensorManager
import android.os.Bundle
import android.provider.CalendarContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat.enableEdgeToEdge
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
                    ListItems(
                        mod = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

}

data class ShoppingItem(var name:String , var sel:Boolean)

@Composable
fun ListItems( mod: Modifier = Modifier) {

    var selectedItem = remember { mutableStateOf<ShoppingItem?>(null) }

    //var selectedItem: ShoppingItem? = null

    var newItem = rememberSaveable { mutableStateOf("")}

    val items =  remember{ mutableStateListOf<ShoppingItem>()  }

    Column(modifier = mod.fillMaxWidth() ) {
        //This row is for adding new items
        Row {
            TextField(value = newItem.value, onValueChange = { newStr -> newItem.value = newStr })
            Button(onClick = {
                var newShopItem = ShoppingItem(newItem.value, false)
                items.add(  newShopItem );
                newItem.value = "" }) {
                Text("Add item")
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()) {
            items(items.size) { rowNum ->
              Row(  modifier=Modifier.fillMaxWidth(), verticalAlignment  = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween)
              {
                  Text(text = "Item: ${items[rowNum].name}")
                  Checkbox(checked = items[rowNum].sel,
                      onCheckedChange = {newVal ->
                          items[rowNum] = ShoppingItem(items[rowNum].name, newVal) } )
              }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyAndroidLabsTheme {
        ListItems()
    }
}