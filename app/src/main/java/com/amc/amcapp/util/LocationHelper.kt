package com.amc.amcapp.util

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient

class LocationHelper {
    @SuppressLint("MissingPermission")
    fun getLastLocation(
        fusedLocationClient: FusedLocationProviderClient, onLocationResult: (Location?) -> Unit
    ) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            onLocationResult(location)
        }.addOnFailureListener {
            onLocationResult(null)
        }
    }
}