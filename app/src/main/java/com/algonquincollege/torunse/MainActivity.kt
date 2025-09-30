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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.algonquincollege.torunse.ui.theme.MyAndroidLabsTheme

//https://developer.android.com/develop/ui/compose/tooling/iterative-development


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            MyAndroidLabsTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    DisplayText(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayText(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val mainKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val sharedPreferences = EncryptedSharedPreferences.create(
        "MyFileName" ,
        mainKey,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    //for showing the dialog, starts as true
    var isShowingDialog = remember {mutableStateOf(true)}
    var agreeCollectData = remember{mutableStateOf(false) }

                                                        //!! is non-null assertion:
    var currentValue = rememberSaveable() {mutableStateOf(sharedPreferences.getString("FieldInput", "")!!) }
    Column {

        Text(text = "Our currentValue is ${currentValue.value}", modifier = modifier)
        TextField(
            placeholder = { Text("This hints what the text should be") },
            label = { Text("This describes the textfield") },
            value = currentValue.value,         //this causes recomposition:
            onValueChange = { newValue -> currentValue.value = newValue })

    }
    if(isShowingDialog.value)
        AlertDialog(
            onDismissRequest = {isShowingDialog.value = false},
            title = { Text(text = "Dialog Title") },
            text = { Text("Here is a text ") },       //This below causes a recomposition
            confirmButton = {  Button( onClick = {
                agreeCollectData.value = true
                isShowingDialog.value = false }) { Text("I agree")   }  },
            dismissButton = {  Button( onClick = { isShowingDialog.value = false }) {Text("Cancel")    }  }
        )

    if(agreeCollectData.value) {
        with(sharedPreferences.edit()) {
            putString("FieldInput", currentValue.value) //what the user typed
            apply()//this writes to disk
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyAndroidLabsTheme {
        DisplayText()
    }
}

/*encryption code:
val context = LocalContext.current
val mainKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

val sharedPreferences = EncryptedSharedPreferences.create(
    "MyFileName" ,
    mainKey,
    context,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)

 */