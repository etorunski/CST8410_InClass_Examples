package com.algonquincollege.torunse

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import  android.hardware.SensorManager
import android.os.Bundle
import android.provider.CalendarContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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

        setContent {

            var value = remember { mutableStateOf(0.0f) }
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

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ListItems( mod: Modifier = Modifier) {

    var selectedItem = rememberSaveable { mutableStateOf<ShoppingItem?>(null) }

    var newItem = rememberSaveable { mutableStateOf("")}

    val items =  remember{ mutableStateListOf<ShoppingItem>()  }

     val widthSizeClass = calculateWindowSizeClass(LocalActivity.current!!)
    val isTablet = widthSizeClass.widthSizeClass  == WindowWidthSizeClass.Expanded

    var rowWidth = 1.0f
    if(isTablet)
        rowWidth = 0.3f

    if(isTablet or (selectedItem.value == null)) //if we're on a tablet or phone showing list
    {

            Row() {
                Column(modifier = mod.fillMaxWidth(rowWidth)) {
                    //This row is for adding new items
                    Row {
                        TextField(
                            value = newItem.value,
                            onValueChange = { newStr -> newItem.value = newStr })
                        Button(onClick = {
                            var newShopItem = ShoppingItem(newItem.value, false)

                            //since this is a mutableStateList variable, this will cause a recomposition:
                            items.add(newShopItem);
                            newItem.value = ""
                        }) {
                            Text("Add item")
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(items.size) { rowNum ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clickable(onClick = { selectedItem.value = items[rowNum] }),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            )
                            {
                                Text(text = "Item: ${items[rowNum].name}")
                                Checkbox(
                                    checked = items[rowNum].sel,
                                    onCheckedChange = { newVal ->
                                        items[rowNum] = ShoppingItem(items[rowNum].name, newVal)
                                    })
                            }
                        }
                    }
                }
                selectedItem.value?.let {
                    Column(modifier = mod.fillMaxWidth(1.0f - rowWidth))
                    {
                        ItemDetails(selectedItem, mod)
                    }

                }
            }//if selected item == null

    }
   else //there's an item selected
    {
        ItemDetails(selectedItem, mod) //This shows the details page on the whole page
    }
}

    @Composable
    fun ItemDetails(selectedItem: MutableState<ShoppingItem?>, mod: Modifier = Modifier){
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                Text(selectedItem.value!!.name)
                Text(selectedItem.value!!.sel.toString())
            }
            Button(modifier=Modifier.align(Alignment.BottomStart),  onClick = { selectedItem.value = null }) {
                Text("Hide")
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