package com.example.gohike.data.profile

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileDataSource {
    // Get a reference to our posts
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    var ref: DatabaseReference = database.getReference("server/saving-data/fireblog/posts")

    fun loadProfileData() {

    }
}