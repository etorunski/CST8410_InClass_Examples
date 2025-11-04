package com.algonquincollege.torunse


import android.content.ClipData
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Room
import com.algonquincollege.torunse.ui.theme.MyAndroidLabsTheme

//https://developer.android.com/develop/ui/compose/tooling/iterative-development


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()



        //get the database:
        val db = Room.databaseBuilder(this@MainActivity, ItemDatabase::class.java, "TheFilename.db").build()
        //retrieve the DAO:
        val mDAO = db.getMyDAO()


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

@Entity(tableName="Items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id:Int,
    @ColumnInfo(name = "name")
    var name:String ,

    @ColumnInfo(name = "sel")
    var sel:Boolean)

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