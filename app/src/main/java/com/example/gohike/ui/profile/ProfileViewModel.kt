package com.example.gohike.ui.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gohike.connection.ConnectionThread
import com.example.gohike.connection.profile.ProfileHandle
import com.example.gohike.connection.profile.ProfileRequestEvent
import com.example.gohike.connection.profile.ProfileRetrievalFailureEvent
import com.example.gohike.connection.profile.ProfileRetrievalSuccessEvent
import com.example.gohike.connection.profile.ProfileSaveEvent
import com.example.gohike.data.profile.Profile
import com.google.firebase.Timestamp

class ProfileViewModel : ViewModel(), ProfileHandle {
    private val TAG : String = "ProfileViewModel"

    var profileState : MutableLiveData<ProfileState> = MutableLiveData(ProfileState.UNLOADED)

    var _error = MutableLiveData<String>()

    var _FirstName = MutableLiveData<String>()
    var _LastName = MutableLiveData<String>()
    var _PhoneNumber = MutableLiveData<String>()
    var _Gender = MutableLiveData<String>()
    var _BirthDate = MutableLiveData<String>()

    init {
        ConnectionThread.addProfileHandler(this)
    }

    fun requestProfile() {
        ConnectionThread.addEvent(ProfileRequestEvent())
        profileState.value = ProfileState.LOADING
    }

    fun saveProfile(
        firstName: String?,
        lastName: String?,
        phoneNumber: String?,
        birthDate: String?,
        gender: String?
    ) {
        var profileToSave = Profile()

        if (firstName != null) {
            profileToSave.firstName = firstName
        }

        if (lastName != null) {
            profileToSave.lastName = lastName
        }

        if (phoneNumber != null) {
            profileToSave.phoneNumber = phoneNumber
        }

        if (birthDate != null) {
            profileToSave.birthDate = birthDate
        }

        if (birthDate != null) {
            profileToSave.gender = gender
        }

        Log.i(TAG, "Saving profile $profileToSave")
        ConnectionThread.addEvent(ProfileSaveEvent(profileToSave))
    }

    fun setError(error : String) {
        profileState.postValue(ProfileState.ERROR)
        _error.value = error
        Log.e(TAG,error)
    }

    override fun onProfileRetrieveSuccess(event: ProfileRetrievalSuccessEvent) {
        val profile = event.profile
        var isComplete = false

        _FirstName.postValue(profile.firstName)
        _LastName.postValue(profile.lastName)
        _PhoneNumber.postValue(profile.phoneNumber)
        _BirthDate.postValue(profile.birthDate)
        _Gender.postValue(profile.gender)

        // definition of "complete profile"
        if (_FirstName.value != null && _LastName.value != null && _PhoneNumber.value != null)
            isComplete = true

        if (isComplete) {
            profileState.postValue(ProfileState.LOADED_COMPLETE)
        } else {
            profileState.postValue(ProfileState.LOADED_INCOMPLETE)
        }
    }

    override fun onProfileRetrieveFailure(event: ProfileRetrievalFailureEvent) {
        profileState.postValue(ProfileState.ERROR)
    }
}

enum class ProfileState {
    // states of profile that has not logged in yet
    UNLOADED,

    // state of profile that tries to log in
    LOADING,

    // states of profile that logged in
    LOADED_INCOMPLETE,
    LOADED_COMPLETE,

    // error state
    ERROR
}

