package com.example.gohike.data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.gohike.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.Scanner
import kotlin.reflect.KFunction

class NotificationSource() {
    val TAG = "NotificationSource"
    var getContext: KFunction<Context>? = null

    private val NotificationChannelId : String = "NewRoutesNotification"
    private val NotificationChannelName : String = "GoHike New Routes"
    private val NotificationChannelDescription : String = "GoHike channel that notifies " +
            "about the apparition of new routes"

    // Once user logged in and loaded a list of routes,
    // the amount of routes will be stored in cache

    // When there appear more than THRESHOLD new routes in
    // database, the user will be notified
    private val THRESHOLD = 1
    private val database = Firebase.firestore
    private var newRouteNum = 0L

    // needs to be done here because we do not have context at init
    private var notificationChannelExists = false

    init {
        initAux()
    }

    fun initAux() {
        val contextExternal = getContext?.call()

        if (contextExternal == null) {
            Log.w(TAG, "No context available")
        } else {
            createNotificationChannel(contextExternal)
            notificationChannelExists = true
        }

        database
            .collection("notifications")
            .document("routes-number")
            .addSnapshotListener {snapshot, e ->
                val context = getContext?.call()

                if (e != null) {
                    Log.w(TAG, "Listen failed", e)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    Log.w(TAG, "No snapshot available")
                    return@addSnapshotListener
                }

                if (context == null) {
                    Log.w(TAG, "No context available")
                    return@addSnapshotListener
                }

                if (!notificationChannelExists) {
                    Log.w(TAG, "Snapshot receuved, but no NotificationChannel exists!")
                    createNotificationChannel(context)
                    notificationChannelExists = true
                }

                try {
                    val newPossibleRouteNum = snapshot.getLong("number")

                    if (newPossibleRouteNum == null) {
                        Log.e(TAG, "Ammount of routes not found!")
                        return@addSnapshotListener
                    }

                    newRouteNum = newPossibleRouteNum
                } catch (e : RuntimeException) {
                    Log.e(TAG, "Ammount of routes read is not a number!", e)
                    return@addSnapshotListener
                }

                var notificationsFile = File(context.cacheDir, "notifications")
                if (!notificationsFile.exists()) {
                    Log.w(TAG, "Notifications file did not exist")
                    notificationsFile.createNewFile()
                }

                var scanner = Scanner(BufferedReader(FileReader(notificationsFile.path)))
                if (scanner.hasNextLong()) {
                    val oldRouteNum = scanner.nextLong()
                    if (oldRouteNum + THRESHOLD <= newRouteNum) {
                        notifyNewRoutes(context, newRouteNum - oldRouteNum)
                        writeRouteNumCash(notificationsFile, newRouteNum)
                    }

                    if (newRouteNum < oldRouteNum) {
                        writeRouteNumCash(notificationsFile, newRouteNum)
                    }
                } else {
                    writeRouteNumCash(notificationsFile, newRouteNum)
                }
            }
    }

    fun writeRouteNumCash(notificationFile : File, routeNum: Long) {
        if (!notificationFile.exists()) {
            notificationFile.createNewFile()
        }

        var fileWriter = FileWriter(notificationFile)
        fileWriter.write(routeNum.toString())
        fileWriter.flush()
        fileWriter.close()
    }

    fun notifyNewRoutes(context : Context, routeNum : Long) {

        var builder = NotificationCompat.Builder(context, NotificationChannelId)
            .setSmallIcon(R.drawable.baseline_explore_24)
            .setContentTitle("$routeNum rute noi!")
            .setContentText("Au aparut $routeNum rute noi care te-ar putea interesa!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(0, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = NotificationChannelName
            val descriptionText = NotificationChannelDescription
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NotificationChannelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}