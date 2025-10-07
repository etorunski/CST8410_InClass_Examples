package com.algonquincollege.torunse


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.algonquincollege.torunse.ui.theme.MyAndroidLabsTheme


class SecondActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

                                        //getter functions:
        var name = DataRepository.theInstance.Name
        var age = DataRepository.theInstance.Age


        setContent {

            MyAndroidLabsTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    SecondPageContent(m = Modifier.padding(innerPadding) )
                }
            }
        }
    }
}

@Composable
fun SecondPageContent( m: Modifier = Modifier) {
    val context = LocalActivity.current //this page


    var email = ""

    //get the data from the intent that launched this activity:
    val data: Uri? = context?.intent?.data

    if(data != null)
    {
        email = data.getQueryParameter("email") ?: "Unknown"
    }


    Column(
        modifier = m.fillMaxSize(),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally ) {

        Text("Name: ${DataRepository.theInstance.Name}, Age=${DataRepository.theInstance.Age}")
        Button(onClick = {

                context?.finish() //go back to previous page

        }) { Text("Go back!!") }
    } //end of Column
}

@Preview(showBackground = true)
@Composable
fun SecondPageContentPreview() {
    MyAndroidLabsTheme {
        SecondPageContent()
    }
}
