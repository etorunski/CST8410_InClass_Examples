package com.algonquincollege.torunse

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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Room
import com.algonquincollege.torunse.ui.theme.MyAndroidLabsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

//https://developer.android.com/develop/ui/compose/tooling/iterative-development


class MainActivity : ComponentActivity() {

    var itemsList = MutableLiveData<ShoppingItem>()

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
                        mod = Modifier.padding(innerPadding),
                        mDAO

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
    var id:Int = 0,

    @ColumnInfo(name = "name")
    var name:String = "",

    @ColumnInfo(name = "sel")
    var sel:Boolean = false)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ListItems( mod: Modifier = Modifier, theDAO: ItemDAO ) {

    var selectedItem = rememberSaveable { mutableStateOf<ShoppingItem?>(null) }

    var newItem = rememberSaveable { mutableStateOf("")}

    val shoppingItems = rememberSaveable{mutableStateListOf<ShoppingItem>() }

    run{ //Need this to be outside of Composable scope

        CoroutineScope(Dispatchers.IO).launch{
            if(shoppingItems.isEmpty())
            {
                val itms =  theDAO.getAllItems() //list
                shoppingItems.clear()
                shoppingItems.addAll(itms)
            }

        }
    }

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
                            onValueChange = { newStr -> newItem.value = newStr },
                            modifier = Modifier.testTag("Input")
                            )
                        Button(onClick = {        //0 for autogenerate
                            var newShopItem = ShoppingItem(0,newItem.value, false)

                            shoppingItems.add(newShopItem)

                            //launch on an I/O background thread:

                            CoroutineScope(Dispatchers.IO).launch{

                                //insert into database:
                                val id = theDAO.insertMessage (newShopItem)
                                id?.let{
                                    newShopItem.id = it.toInt()
                                }

                            }
                            newItem.value = ""
                        }, modifier = Modifier.testTag("add")) {
                            Text("Add item")
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(shoppingItems) { rowNum, entity ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clickable(onClick = { selectedItem.value = entity }),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            )
                            {
                                Text(text = "Item: ${entity.name}", modifier = Modifier.testTag("item${rowNum}"))
                                Checkbox(
                                    checked = entity.sel,
                                    onCheckedChange = { newVal ->

                                        shoppingItems[rowNum] = ShoppingItem(entity.id, entity.name, newVal)
                                        entity.sel = newVal

                                        CoroutineScope(Dispatchers.IO).launch {
                                            theDAO.updateMessage(entity)
                                        }
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
    //get the database:
    val db = Room.databaseBuilder(LocalContext.current, ItemDatabase::class.java, "TheFilename.db").build()
    //retrieve the DAO:
    val mDAO = db.getMyDAO()


    MyAndroidLabsTheme {
        ListItems(mod=Modifier.padding(5.dp), mDAO)
    }
}