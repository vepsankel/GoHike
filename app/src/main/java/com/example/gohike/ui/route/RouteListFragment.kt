package com.example.gohike.ui.route

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.gohike.R
import com.example.gohike.connection.ConnectionThread
import com.example.gohike.connection.route.RouteRetrievalSuccessEvent
import com.example.gohike.data.route.Route
import com.example.gohike.databinding.FragmentRouteListBinding

/**
 * A fragment representing a list of Items.
 */
class RouteListFragment : Fragment() {
    private lateinit var viewModel: RouteListViewModel
    private lateinit var list : RecyclerView
    private lateinit var addRouteButton : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // this line is needed so that we could inform RouteFragment to set its state to DISPLAYING
        ViewModelProvider(requireActivity()).get(RouteViewModel::class.java)
        viewModel = ViewModelProvider(this).get(RouteListViewModel::class.java)

        val binding = FragmentRouteListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        list = binding.list
        viewModel.list.observe(viewLifecycleOwner) {
            list.adapter = MyRouteRecyclerViewAdapter(it)
        }

        list.layoutManager = LinearLayoutManager(context)
        if (list.adapter == null)
            list.adapter = MyRouteRecyclerViewAdapter(ArrayList<Route>())

        addRouteButton = binding.routeListAddRoute

        viewModel.uid.observe(viewLifecycleOwner) {
            if (it != null)
                addRouteButton.visibility = View.VISIBLE
            else
                addRouteButton.visibility = View.GONE
        }

        addRouteButton.setOnClickListener {
            val userId = viewModel.uid.value ?: return@setOnClickListener

            val r = Route().buildCreatorUid(userId)
            ConnectionThread.addEvent(RouteRetrievalSuccessEvent(r))
            it.findNavController().navigate(R.id.routeFragment)
        }

        viewModel.getRouteList()
        return root
    }
}