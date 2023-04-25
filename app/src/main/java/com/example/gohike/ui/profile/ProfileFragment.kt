package com.example.gohike.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.gohike.databinding.FragmentProfileBinding
import com.google.android.material.textfield.TextInputLayout

class ProfileFragment : Fragment() {
    private lateinit var loading: ProgressBar
    private lateinit var info: ScrollView
    private lateinit var profileIncompleteWarning: TextView

    private lateinit var firstName: TextInputLayout
    private lateinit var lastName: TextInputLayout
    private lateinit var phoneNumber: TextInputLayout
    private lateinit var birthDate: TextInputLayout
    private lateinit var gender: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        profileViewModel.profileState.observe(viewLifecycleOwner, Observer {
            updateProfileState(it)
        })

        val binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        loading = binding.profileLoading
        info = binding.profileInfo
        profileIncompleteWarning = binding.emptyProfileText

        firstName = binding.firstName
        lastName = binding.lastName
        phoneNumber = binding.phoneNumber
        birthDate = binding.birthDate
        gender = binding.gender

        binding.saveButton.setOnClickListener {
            saveProfile()
        }

        profileViewModel.requestProfile()

        return root
    }

    private fun saveProfile() {
        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        profileViewModel.saveProfile(
            firstName.editText?.text.toString(),
            lastName.editText?.text.toString(),
            phoneNumber.editText?.text.toString(),
            birthDate.editText?.text.toString(),
            gender.editText?.text.toString()
        )
    }
    private fun updateProfileState(state : ProfileState) {
        when(state) {
            ProfileState.UNLOADED -> updateProfileStateToUnloaded()
            ProfileState.LOADING -> updateProfileStateToLoading()
            ProfileState.LOADED_INCOMPLETE -> updateProfileStateToLoadedIncomplete()
            ProfileState.LOADED_COMPLETE -> updateProfileStateToLoadedComplete()
            ProfileState.ERROR -> updateProfileStateToError()
        }
    }

    private fun updateProfileStateToError() {
        loading.visibility = View.INVISIBLE
        info.visibility = View.INVISIBLE

        Log.i("ProfileFragment","Switched to ERROR state")
    }

    private fun updateProfileStateToLoadedComplete() {
        loading.visibility = View.INVISIBLE
        info.visibility = View.VISIBLE
        profileIncompleteWarning.visibility = View.INVISIBLE

        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        firstName.editText?.setText(profileViewModel._FirstName.value)
        lastName.editText?.setText(profileViewModel._LastName.value)
        phoneNumber.editText?.setText(profileViewModel._PhoneNumber.value)
        gender.editText?.setText(profileViewModel._Gender.value)
        birthDate.editText?.setText(profileViewModel._BirthDate.value)

        Log.i("ProfileFragment","Switched to LOADED_COMPLETE state")
    }

    private fun updateProfileStateToLoadedIncomplete() {
        loading.visibility = View.INVISIBLE
        info.visibility = View.VISIBLE
        profileIncompleteWarning.visibility = View.VISIBLE

        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        firstName.editText?.setText(profileViewModel._FirstName.value)
        lastName.editText?.setText(profileViewModel._LastName.value)

        Log.i("ProfileFragment","Switched to LOADED_INCOMPLETE state")
    }

    private fun updateProfileStateToUnloaded() {
        loading.visibility = View.INVISIBLE
        info.visibility = View.INVISIBLE

        Log.i("ProfileFragment","Switched to UNLOADED state")

        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        profileViewModel.requestProfile()
    }

    private fun updateProfileStateToLoading() {
        loading.visibility = View.VISIBLE
        info.visibility = View.INVISIBLE

        Log.i("ProfileFragment","Switched to LOADING state")
    }
}