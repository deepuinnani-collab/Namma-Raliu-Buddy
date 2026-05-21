package com.example.nammarailubuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng

import com.google.firebase.database.*

import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

class MapActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var position by remember {
                mutableStateOf(LatLng(12.97, 77.59))
            }

            val cameraPositionState = rememberCameraPositionState()

            val database = FirebaseDatabase.getInstance()
            val ref = database.getReference("train_location")

            // Firebase realtime updates
            LaunchedEffect(Unit) {

                ref.addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        val lat =
                            snapshot.child("latitude")
                                .getValue(Double::class.java)

                        val lng =
                            snapshot.child("longitude")
                                .getValue(Double::class.java)

                        if (lat != null && lng != null) {

                            position = LatLng(lat, lng)

                            cameraPositionState.move(
                                CameraUpdateFactory.newLatLngZoom(
                                    position,
                                    15f
                                )
                            )
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {

                Marker(
                    state = MarkerState(position),
                    title = "Live Train"
                )
            }
        }
    }
}