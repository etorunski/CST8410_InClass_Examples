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
import androidx.compose.ui.Modifier
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
                    DisplayText(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
@Composable
fun DisplayText(modifier: Modifier = Modifier) {

    var currentValue = remember {mutableStateOf("Hello world") }

    Text( text = "Our currentValue is ${currentValue.value}", modifier = modifier  )
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