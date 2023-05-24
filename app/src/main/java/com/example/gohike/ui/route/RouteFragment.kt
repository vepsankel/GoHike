package com.example.gohike.ui.route

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.navigation.findNavController
import com.example.gohike.R
import com.example.gohike.databinding.FragmentRouteBinding
import com.google.android.material.textfield.TextInputLayout

class RouteFragment : Fragment() {
    private val TAG = "RouteFragment"

    private lateinit var viewModel: RouteViewModel
    private lateinit var routeHeader: TextView
    private lateinit var editRouteButton: Button
    private lateinit var routeName: TextInputLayout
    private lateinit var routeLocation: TextInputLayout
    private lateinit var routeDifficulty: RatingBar
    private lateinit var routeDifficultyDescription: TextView
    private lateinit var routeNecessaryTimeTime: TextView
    private lateinit var routeNecessaryTimeButtonGroups: LinearLayout
    private lateinit var minus15m: Button
    private lateinit var minusH: Button
    private lateinit var plus15m: Button
    private lateinit var plusH: Button
    private lateinit var routeDescription: EditText
    private lateinit var saveButton: Button
    private lateinit var routeMapInviter: TextView
    private lateinit var routeToMap: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[RouteViewModel::class.java]

        val binding = FragmentRouteBinding.inflate(inflater, container, false)
        val view = binding.root

        routeHeader = binding.routeHeader
        editRouteButton = binding.routeEdit
        routeName = binding.routeName
        routeLocation = binding.routeLocation
        routeDifficulty = binding.routeDifficulty
        routeDifficultyDescription = binding.routeDifficultyDescription
        routeNecessaryTimeTime = binding.routeNecessaryTimeTime
        routeNecessaryTimeButtonGroups = binding.routeNecessaryTimeButtonsGroup
        plusH = binding.plusH
        plus15m = binding.plus15m
        minusH = binding.minusH
        minus15m = binding.minus15m
        routeDescription = binding.routeDescription
        saveButton = binding.routeSaveButton
        routeMapInviter = binding.routeMapInviter
        routeToMap = binding.routeToMap

        editRouteButton.setOnClickListener {
            viewModel.edit()
        }

        routeDifficulty.setOnRatingBarChangeListener { ratingBar: RatingBar, rate: Float, fromUser: Boolean ->
            if (rate <= 0.6) {
                routeDifficultyDescription.text = "Easy, like a midday latte, so easy even a toddler could do it"
            } else if (rate <= 1.6) {
                routeDifficultyDescription.text = "Moderate, Like a cappuccino, a bit more challenging but still enjoyable."
            } else if (rate <= 2.6) {
                routeDifficultyDescription.text = "Challenging, like an Americano, not for the faint of heart but will keep you going."
            } else if (rate <= 3.6) {
                routeDifficultyDescription.text = "Difficult, like an espresso. Strong, intense, and not for the weak"
            } else if (rate <= 4.6) {
                routeDifficultyDescription.text = "Extreme, like a double espresso. Only for the bravest and most daring hikers"
            }
        }

        plusH.setOnClickListener {
            viewModel.setTimeInc(4)
            updateTime()
        }

        plus15m.setOnClickListener {
            viewModel.setTimeInc(1)
            updateTime()
        }

        minus15m.setOnClickListener {
            viewModel.setTimeInc(-1)
            updateTime()
        }

        minusH.setOnClickListener {
            viewModel.setTimeInc(-4)
            updateTime()
        }

        viewModel.state.observe(viewLifecycleOwner) {
            updateRouteState(it)
        }

        saveButton.setOnClickListener {
            saveRoute()
        }

        routeToMap.setOnClickListener {
            it.findNavController().navigate(R.id.action_navigation_route_to_routeMapFragment)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (viewModel.state.value == RouteState.ROUTE_EDITING)
            viewModel.saveRouteLocally(
                routeName.editText?.text.toString(),
                routeLocation.editText?.text.toString(),
                routeDifficulty.rating.toInt(),
                routeDescription.text?.toString(),
            )
    }

    private fun updateRouteState(state : RouteState) {
        when (state) {
            RouteState.ROUTE_DISPLAYING -> displayConstantRoute()
            RouteState.ROUTE_EDITING -> displayEditingRoute()
            RouteState.ROUTE_ERROR -> n()
        }
    }

    private fun updateTime() {
        val time = viewModel.getTime()
        routeNecessaryTimeTime.setText("${(time / 4)}h${(time % 4) * 15}m")
    }

    private fun displayConstantRoute(
    ) {
        // change visibility of the elements
        routeNecessaryTimeButtonGroups.visibility = View.GONE
        saveButton.visibility = View.GONE
        if (viewModel.canEditThisRoute)
            editRouteButton.visibility = View.VISIBLE
        else
            editRouteButton.visibility = View.GONE
        routeMapInviter.visibility = View.GONE

        // set fields value
        routeHeader.setText("Viewing ${viewModel.getName()}")
        routeName.editText?.setText(viewModel.getName())
        routeLocation.editText?.setText(viewModel.getLocation())
        routeDifficulty.rating = viewModel.getDifficulty().toFloat()
        routeDescription.setText(viewModel.getDescription())
        updateTime()

        // change editable status of the fields
        routeName.editText?.inputType = EditorInfo.TYPE_NULL
        routeLocation.editText?.inputType = EditorInfo.TYPE_NULL
        routeDifficulty.setIsIndicator(true)
        routeDescription.inputType = (EditorInfo.TYPE_CLASS_TEXT  or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        routeDescription.isFocusableInTouchMode = false
        routeDescription.isFocusable = false

        Log.i(TAG,"Switched to ROUTE_DISPLAYING state")
    }

    private fun displayEditingRoute() {
        // change visibility of the elements
        routeNecessaryTimeButtonGroups.visibility = View.VISIBLE
        saveButton.visibility = View.VISIBLE
        editRouteButton.visibility = View.GONE
        routeMapInviter.visibility = View.VISIBLE

        // set fields value
        if (viewModel.getName().compareTo("") != 0)
            routeHeader.setText("Editing ${viewModel.getName()}")
        else
            routeHeader.setText("Editing new route")
        routeName.editText?.setText(viewModel.getName())
        routeLocation.editText?.setText(viewModel.getLocation())
        routeDifficulty.rating = viewModel.getDifficulty().toFloat()
        routeDescription.setText(viewModel.getDescription())
        updateTime()

        // change editable status of the fields
        routeName.editText?.inputType = EditorInfo.TYPE_CLASS_TEXT
        routeLocation.editText?.inputType = EditorInfo.TYPE_CLASS_TEXT
        routeDifficulty.setIsIndicator(false)
        routeDescription.inputType = (EditorInfo.TYPE_CLASS_TEXT  or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        routeDescription.isFocusableInTouchMode = true;
        routeDescription.isFocusable = true

        Log.i(TAG,"Switched to ROUTE_EDITING state")
    }

    private fun n() {
        Log.i(TAG,"Switched to n() state")
    }

    fun saveRoute() {
        viewModel.saveRoute(
            routeName.editText?.text.toString(),
            routeLocation.editText?.text.toString(),
            routeDifficulty.rating.toInt(),
            routeDescription.text?.toString(),
        )
    }
}