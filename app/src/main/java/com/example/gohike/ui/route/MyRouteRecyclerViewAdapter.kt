package com.example.gohike.ui.route

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.gohike.R
import com.example.gohike.connection.ConnectionThread
import com.example.gohike.connection.route.RouteRetrievalSuccessEvent
import com.example.gohike.data.route.Route

class MyRouteRecyclerViewAdapter(
    private val values: List<Route>
) : RecyclerView.Adapter<MyRouteRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_route_mini, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        var difficultyText = ""

        difficultyText = when (item.difficulty) {
            1 -> "Moderate"
            2 -> "Challenging"
            3 -> "Difficult"
            4 -> "Extreme"
            else -> "Easy"
        }

        holder.name.text = item.name
        holder.location.text = item.location
        val time : Int = item.time ?: 0
        holder.time.text = "${(time / 4)}h${(time % 4) * 15}m"
        holder.difficulty.text = difficultyText

        holder.route.setOnClickListener {
            it.findNavController().navigate(R.id.routeFragment)
            ConnectionThread.addEvent(RouteRetrievalSuccessEvent(values[position]))
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val location: TextView
        val difficulty: TextView
        val time: TextView
        val route: LinearLayout

        init {
            name = view.findViewById(R.id.mini_route_name)
            location = view.findViewById(R.id.mini_route_location)
            difficulty = view.findViewById(R.id.mini_route_difficulty)
            time = view.findViewById(R.id.mini_route_time)
            route = view.findViewById(R.id.mini_route)
        }
    }

}