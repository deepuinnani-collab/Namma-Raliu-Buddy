package com.example.nammarailubuddy

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import kotlin.math.*
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationTrackingService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val destLat = 12.9716
    private val destLng = 77.5946

    override fun onCreate() {
        super.onCreate()

        startForegroundServiceNotification()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000
        ).build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.requestLocationUpdates(
            request,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {

                    val loc = result.lastLocation ?: return

                    val distance = calculateDistance(
                        loc.latitude,
                        loc.longitude,
                        destLat,
                        destLng
                    )

                    if (distance <= 5000) {
                        NotificationHelper.show(
                            applicationContext,
                            "🚨 Wake Up Alert",
                            "You are 5 KM near your destination!"
                        )
                    }
                }
            },
            mainLooper
        )
    }

    private fun startForegroundServiceNotification() {

        val channelId = "rail_tracking"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Rail Tracking",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("🚆 Rail Tracking Active")
            .setContentText("Tracking your journey...")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()

        startForeground(1, notification)
    }

    private fun calculateDistance(lat1: Double, lon1: Double,
                                  lat2: Double, lon2: Double): Double {

        val R = 6371e3

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }

    override fun onBind(intent: Intent?): IBinder? = null
}