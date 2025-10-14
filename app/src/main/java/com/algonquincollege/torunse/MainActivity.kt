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
import kotlinx.serialization.Serializable
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName


@Serializable
data class LoginRequest(
    @SerialName("loginName")
var loginName: String?,
@SerialName("password")
var password: String?
)


//https://developer.android.com/develop/ui/compose/tooling/iterative-development








class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()



        setContent {

            MyAndroidLabsTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    DisplayLighting(3.5f,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

}

@Composable
fun DisplayLighting(lightingValue: Float, modifier: Modifier = Modifier) {

    val client = HttpClient(Android){
        install(ContentNegotiation) {
            json()
        }
    }

    Column(modifier = Modifier.fillMaxSize(1.0f),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
        Button({
            CoroutineScope(Dispatchers.IO).launch {
                //Do you stuff here in a background thread.
                try {
                val response: HttpResponse = client.post("http://10.0.2.2:8080/firstTest")
                {

                        contentType(ContentType.Application.Json)
                        val login = LoginRequest("Jet", "Brains")
                        setBody(login) //automatically serialize to JSON

                }
                    val ans = response.bodyAsText()
                println(response.status)
                }catch(e: Exception){
            e.message
        }

            }


        } ){
            Text("Login")
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