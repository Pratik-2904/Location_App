package com.example.location_app

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {
    //initialize location with null
    private val _location = mutableStateOf<LocationData?>(null)

    //accessible variable of same type and state as _location even after the change dynamically
    val location: State<LocationData?> = _location

    fun updateLocation(
        newLocation: LocationData
    ) {
        _location.value = newLocation
    }

}