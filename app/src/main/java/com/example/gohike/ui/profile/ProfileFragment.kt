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
import com.example.gohike.connection.ConnectionThread
import com.example.gohike.connection.login.LogoutEvent
import com.example.gohike.databinding.FragmentProfileBinding
import com.google.android.material.textfield.TextInputLayout

class ProfileFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel

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
        profileViewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]
        profileViewModel.requestProfile()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        profileViewModel.profileState.observe(viewLifecycleOwner, Observer {
            updateProfileState(it)
        })

        // Inflate the layout for this fragment
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

        binding.logoutButton.setOnClickListener {
            ConnectionThread.addEvent(LogoutEvent())
        }

        //profileViewModel.requestProfile()
        //profileViewModel.profileState.value?.let { updateProfileState(it) }

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

        firstName.editText?.setText(profileViewModel._FirstName)
        lastName.editText?.setText(profileViewModel._LastName)
        phoneNumber.editText?.setText(profileViewModel._PhoneNumber)
        gender.editText?.setText(profileViewModel._Gender)
        birthDate.editText?.setText(profileViewModel._BirthDate)

        Log.i("ProfileFragment","Switched to LOADED_COMPLETE state")
    }

    private fun updateProfileStateToLoadedIncomplete() {
        loading.visibility = View.INVISIBLE
        info.visibility = View.VISIBLE
        profileIncompleteWarning.visibility = View.VISIBLE

        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        firstName.editText?.setText(profileViewModel._FirstName)
        lastName.editText?.setText(profileViewModel._LastName)

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