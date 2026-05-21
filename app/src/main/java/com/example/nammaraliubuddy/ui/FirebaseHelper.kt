package com.example.nammarailubuddy

import com.google.firebase.database.*

object FirebaseHelper {

    fun listenPlatformUpdates() {

        val ref = FirebaseDatabase.getInstance()
            .getReference("platform_ping")

        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val platform = snapshot.child("platform").value.toString()
                val msg = snapshot.child("message").value.toString()

                println("Platform: $platform | $msg")
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}