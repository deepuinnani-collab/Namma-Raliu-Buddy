package com.example.nammarailubuddy

import com.google.android.gms.maps.model.LatLng

data class TrainInfo(
    val platform: String,
    val coachPosition: String
)

object TrainDatabase {

    val trainDetails = mapOf(

        "bangalore" to TrainInfo(
            platform = "Platform 5",
            coachPosition = "Near Engine"
        ),

        "majestic" to TrainInfo(
            platform = "Platform 6",
            coachPosition = "Near Engine"
        ),

        "mysore" to TrainInfo(
            platform = "Platform 2",
            coachPosition = "Middle"
        ),

        "chennai" to TrainInfo(
            platform = "Platform 7",
            coachPosition = "Near Guard Coach"
        )
    )
}