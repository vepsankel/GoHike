package com.example.gohike

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.gohike.connection.ConnectionThread
import com.example.gohike.connection.login.LoginHandle
import com.example.gohike.connection.login.LoginSuccessEvent
import com.example.gohike.connection.login.LogoutEvent
import com.example.gohike.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jakewharton.processphoenix.ProcessPhoenix
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), LoginHandle {
    private val TAG : String = "MainActivity"

    private lateinit var binding: ActivityMainBinding
    private lateinit var menu : Menu
    private lateinit var navController: NavController

    init {
        ConnectionThread.addLoginHandler(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        thread {
            ConnectionThread.run(this::getApplicationContext)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        menu = navView.menu

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        this.navController = navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_route_list,
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.navigation_login,
                R.id.navigation_profile,
            )
        )

        menu.getItem(3).isVisible = false
        menu.getItem(1).isVisible = false

        //menu.removeItem(R.id.navigation_profile)
        //menu.removeItem(R.id.navigation_notifications)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onLogout(event: LogoutEvent) {

        ProcessPhoenix.triggerRebirth(applicationContext);

//        runOnUiThread(Runnable {
//            menu.getItem(2).isVisible = true
//
//            menu.getItem(3).isVisible = false
//            menu.getItem(1).isVisible = false
//        })
    }
    override fun onLoginSuccess(event: LoginSuccessEvent) {
        runOnUiThread(Runnable {
            menu.getItem(2).isVisible = false

            menu.getItem(3).isVisible = true
            menu.getItem(1).isVisible = true
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                1)
        }
    }


}