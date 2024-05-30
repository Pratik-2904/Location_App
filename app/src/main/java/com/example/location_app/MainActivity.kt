package com.example.location_app

import android.content.Context
import android.os.Bundle
import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.location_app.ui.theme.Location_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            Location_AppTheme {
                val viewModel : LocationViewModel = viewModel()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(viewModel)
                }
            }
        }
    }



    @Composable
    fun MyApp(
        viewModel: LocationViewModel
    ){
        val context = LocalContext.current
        val locationUtils = LocationUtils(context,viewModel)
        LocationDisplay(locationUtils = locationUtils, context = context, viewModel = viewModel )
    }


    @Composable
    fun LocationDisplay(
        locationUtils: LocationUtils,
        context: Context,
        viewModel: LocationViewModel

    ) {
        val location = viewModel.location.value
        val address = location?.let{
            locationUtils.reverseGeocodeLocation(location)
        }


        val requestPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->
                if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                    &&
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION]==true){
                    //I have access to permission
                    locationUtils.requestLocationUpdates(viewModel = viewModel)
                }
                else{
                    val rationalRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                        context as MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                            ||
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        context as MainActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )

                    if(rationalRequired){
                        Toast.makeText(
                            context,
                            "Location is required for this feature to work",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else
                    {
                        Toast.makeText(
                            context,
                            "Goto settings to enable location permission",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
                })
        
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(location != null){
                Text(text = "Latitude: ${location.latitude}")
                Text(text = "Longitude: ${location.longitude}")
                Text(text = "Address : ${address}")
            }else {
                Text(text = "Location Not Available")
            }
            Button(onClick = {
                if(locationUtils.hasLocationPermission(context)){
                    //Permission already granted
                    locationUtils.requestLocationUpdates(viewModel)
                    }
                    else{
                        // Request location permission
                        requestPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }

            }) {
                Text(text = "Get Location")
            }
        }
    }
}

