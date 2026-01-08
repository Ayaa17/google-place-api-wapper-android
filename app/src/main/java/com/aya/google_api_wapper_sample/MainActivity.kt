package com.aya.google_api_wapper_sample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aya.google_api_wapper.PlaceApiWapper
import com.aya.google_api_wapper.PlaceCallback

class MainActivity : ComponentActivity() {

    val TAG = "MainActivity"
    var placeApi: PlaceApiWapper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Compose UI 狀態
        var resultText by mutableStateOf("結果會顯示在這裡")

        val cb: PlaceCallback = object : PlaceCallback {
            override fun onSuccess(response: Any): Int {
                Log.d(TAG, "onSuccess: $response")
                resultText = response.toString()
                return 0
            }

            override fun onError(error: Throwable): Int {
                Log.d(TAG, "onError: $error")
                resultText = error.toString()
                return 0
            }
        }

        val key = "PUT_KEY_HERE" // fixme: hide sec key
        placeApi = PlaceApiWapper(this, key, cb)


        setContent {
            SimplePlaceUI(
                resultText = resultText,
                onButtonClick = {
//                    val keyword = "台北"
//                    placeApi?.autocompletePlaces(keyword)

                    val placeID = "ChIJcVe4HuirQjQRWHrzxtVRImg" //test 台灣臺北市松山區南京東路四段台北小巨蛋
                    placeApi?.fetchPlaceDetails(placeID)
                }
            )
        }
    }
}

@Composable
fun SimplePlaceUI(
    resultText: String,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = resultText)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { onButtonClick() }) {
            Text("執行 Place API")
        }
    }
}