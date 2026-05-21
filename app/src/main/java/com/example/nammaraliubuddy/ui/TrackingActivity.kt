package com.example.nammarailubuddy

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

import com.google.android.gms.maps.model.LatLng

import com.google.firebase.database.FirebaseDatabase
import androidx.compose.foundation.layout.fillMaxWidth

class TrackingActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    // 🚨 Prevent repeated 5 KM alerts
    private var alertShown = false

    // 🎯 Destination Location
    private val destinationLocation = LatLng(
        13.0827,
        77.5877
    )

    // 🚉 STATIONS
    private val stations = listOf(
        "Station A" to LatLng(12.9716, 77.5946),
        "Station B" to LatLng(13.0827, 77.5877)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val source =
            intent.getStringExtra("source") ?: "Unknown"

        val destination =
            intent.getStringExtra("destination") ?: "Unknown"

        val coach =
            intent.getStringExtra("coach") ?: "General"
        val coachPosition = when (coach.uppercase()) {

            "S1" -> "Near Engine"
            "S2" -> "Near Engine"

            "S3" -> "Middle"
            "S4" -> "Middle"

            "A1" -> "Near Pantry"
            "A2" -> "Near Pantry"

            "GEN" -> "Near Guard Coach"

            else -> "Unknown"
        }

        val station = intent.getStringExtra("station")
            ?.trim()
            ?.lowercase()
            ?: ""

        val trainInfo =
            TrainDatabase.trainDetails[station]

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        // 🔔 Create notification channel
        createNotificationChannel()

        // 🔔 Android 13+ notification permission
        if (
            android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.TIRAMISU
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                200
            )
        }

        setContent {

            MaterialTheme {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    // 🚆 HEADER CARD
                    androidx.compose.material3.Card(
                        modifier = Modifier.fillMaxSize(),
                        elevation = androidx.compose.material3.CardDefaults.cardElevation(6.dp)
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {

                            Text(
                                text = "🚆 Namma Railu Buddy",
                                style = MaterialTheme.typography.headlineMedium
                            )

                            Text(
                                text = "Live Train Tracking Dashboard",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // 📍 ROUTE INFO CARD
                            androidx.compose.material3.Card(
                                elevation = androidx.compose.material3.CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {

                                    Text("📍 From: $source")
                                    Text("🎯 To: $destination")
                                    Text("🧑‍✈️ Coach: $coach")
                                    Text("📦 Position: $coachPosition")
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 🚉 TRAIN INFO CARD
                            androidx.compose.material3.Card(
                                elevation = androidx.compose.material3.CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {

                                    Text(
                                        "🚉 Platform: ${
                                            trainInfo?.platform ?: "Not Available"
                                        }"
                                    )

                                    Text("📡 Firebase Tracking: ACTIVE")
                                    Text("📍 GPS Updates: ON")
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 🟢 STATUS CARD
                            androidx.compose.material3.Card(
                                elevation = androidx.compose.material3.CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {

                                    Text("🟢 Status: TRAIN RUNNING")
                                    Text("⏱ Update Interval: 3 seconds")
                                    Text("🔔 Alerts: Enabled")
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // 🗺 MAP BUTTON
                            Button(
                                onClick = {
                                    startActivity(
                                        Intent(
                                            this@TrackingActivity,
                                            MapActivity::class.java
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(0.5f)
                            ) {
                                Text(
                                    text = "🗺 Open Live Map",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    sendNotification("🔥 TEST NOTIFICATION WORKS")
                                },
                                modifier = Modifier.fillMaxWidth(0.5f)
                            ) {
                                Text(
                                    text = "🔔 Test Notification",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 📡 FOOTER STATUS
                            Text(
                                text = "📍 Live location is continuously updated to Firebase",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        startLocationUpdates()
    }

    // 📍 LOCATION UPDATES
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            3000
        ).build()

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(
                result: LocationResult
            ) {

                for (location in result.locations) {

                    // 🔥 Upload to Firebase
                    sendToFirebase(location)

                    // 🚉 Check stations
                    checkStations(location)

                    // 🚨 Check 5 KM destination alert
                    checkDestinationDistance(location)
                }
            }
        }

        // 🔐 Check location permission
        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                100
            )

            return
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            mainLooper
        )
    }

    // 🔥 FIREBASE UPLOAD
    private fun sendToFirebase(location: Location) {

        val ref = FirebaseDatabase.getInstance()
            .getReference("train_location")

        val data = mapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "timestamp" to System.currentTimeMillis()
        )

        ref.setValue(data)
            .addOnSuccessListener {

                android.util.Log.d(
                    "FIREBASE",
                    "Data uploaded"
                )
            }
            .addOnFailureListener {

                android.util.Log.e(
                    "FIREBASE",
                    "Upload failed: ${it.message}"
                )
            }
    }

    // 🚉 STATION DETECTION
    private fun checkStations(location: Location) {

        for (station in stations) {

            val lat = station.second.latitude
            val lng = station.second.longitude

            val results = FloatArray(1)

            Location.distanceBetween(
                location.latitude,
                location.longitude,
                lat,
                lng,
                results
            )

            // 🚉 500 meters radius
            if (results[0] < 500) {

                sendNotification(
                    "🚉 Train reached ${station.first}"
                )
            }
        }
    }

    // 🚨 5 KM DESTINATION ALERT
    private fun checkDestinationDistance(location: Location) {

        val results = FloatArray(1)

        Location.distanceBetween(
            location.latitude,
            location.longitude,
            destinationLocation.latitude,
            destinationLocation.longitude,
            results
        )

        val distanceInKm = results[0] / 1000

        // 👇 ADD THIS LINE
        android.util.Log.d("DISTANCE", "Distance = $distanceInKm KM")

        if (
            distanceInKm <= 5 &&
            !alertShown
        ) {

            alertShown = true

            sendNotification(
                "🚨 Your destination is within 5 KM"
            )
        }
    }

    // 🔔 NOTIFICATION
    private fun sendNotification(message: String) {

        val builder =
            NotificationCompat.Builder(
                this,
                "train_channel"
            )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_map
                )
                .setContentTitle("🚆 Train Update")
                .setContentText(message)
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )

        val manager =
            NotificationManagerCompat.from(this)

        // 🔐 Notification permission check
        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        manager.notify(
            System.currentTimeMillis().toInt(),
            builder.build()
        )
    }

    // 🔔 NOTIFICATION CHANNEL
    private fun createNotificationChannel() {

        if (
            android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.O
        ) {

            val channel =
                android.app.NotificationChannel(
                    "train_channel",
                    "Train Updates",
                    android.app.NotificationManager.IMPORTANCE_HIGH
                )

            val manager =
                getSystemService(
                    android.app.NotificationManager::class.java
                )

            manager.createNotificationChannel(channel)
        }
    }

    override fun onStop() {
        super.onStop()

        if (::locationCallback.isInitialized) {

            fusedLocationClient.removeLocationUpdates(
                locationCallback
            )
        }
    }
}