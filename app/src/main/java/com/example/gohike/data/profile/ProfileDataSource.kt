package com.example.gohike.data.profile

import android.util.Log
import com.example.gohike.connection.ConnectionThread
import com.example.gohike.connection.profile.ProfileRetrievalFailureEvent
import com.example.gohike.connection.profile.ProfileRetrievalSuccessEvent
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileDataSource {
    private val TAG : String = "ProfileDataSource"

    // Get a reference to our posts
    val database = Firebase.firestore
    var firebaseUser : FirebaseUser? = null

    fun loadProfileData() {
        val firebaseUserCopy = firebaseUser

        if (firebaseUserCopy == null) {
            ConnectionThread.addEvent(ProfileRetrievalFailureEvent("User not logged in"))
            Log.e(TAG,"User not logged!")
            return
        }

        firebaseUserCopy.let {
            database.collection("users").document(firebaseUserCopy.uid).get().addOnSuccessListener{
                var profile = Profile()

                if (it != null) {
                    profile.fromMutableMap(it.data)
                }

                Log.i(TAG,"Profile loaded $profile")
                ConnectionThread.addEvent(ProfileRetrievalSuccessEvent(profile))
            }.addOnFailureListener {
                Log.e(TAG,"FEIL $it")
            }
        }
    }

    fun saveProfileData(profile: Profile) {
        val firebaseUserCopy = firebaseUser

        if (firebaseUserCopy == null) {
            ConnectionThread.addEvent(ProfileRetrievalFailureEvent("User not logged in"))
            Log.e(TAG,"User not logged!")
            return
        }

        firebaseUserCopy.let {
            database.collection("users").document(firebaseUserCopy.uid).set(profile.toMutableMap()).addOnSuccessListener {
                Log.e(TAG,"Profile $profile saved!")
            }.addOnFailureListener {
                Log.w(TAG,"Could not save profile: $it")
            }
        }
    }
}