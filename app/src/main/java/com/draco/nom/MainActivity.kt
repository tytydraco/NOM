package com.draco.nom

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.Display
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class MainActivity: AppCompatActivity() {
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var sharedPrefs: SharedPreferences

    private fun getAppList(): ArrayList<Pair<String, String>> {
        val launcherIntent = Intent(Intent.ACTION_MAIN, null)
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val activities = packageManager.queryIntentActivities(launcherIntent, 0)
        val appList = ArrayList<Pair<String, String>>()

        for (app in activities) {
            val appId = app.activityInfo.packageName
            if (appId == packageName)
                continue

            val info = Pair<String, String>(
                app.activityInfo.loadLabel(packageManager).toString(),
                appId
            )

            appList.add(info)
        }

        appList.sortBy {
            it.first.toLowerCase(Locale.getDefault())
        }

        return appList
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val container = findViewById<LinearLayout>(R.id.container)
        val recycler = findViewById<RecyclerView>(R.id.recycler)

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        recyclerAdapter = RecyclerAdapter(getAppList(), recycler, sharedPrefs, packageManager)
        recyclerAdapter.setHasStableIds(true)

        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val iconSize = resources.getDimension(R.dimen.icon_size) / displayMetrics.density
        val columns = 5.coerceAtLeast((screenWidthDp / iconSize).toInt())

        with (recycler) {
            setItemViewCacheSize(1000)
            adapter = recyclerAdapter
            layoutManager = GridLayoutManager(context, columns)
        }

        if (!sharedPrefs.getBoolean(getString(R.string.pref_rotation), true))
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val backgroundColorString = sharedPrefs.getString(getString(R.string.pref_background_color), "#88000000")
        try {
            val backgroundColor = Color.parseColor(backgroundColorString)
            window.statusBarColor = backgroundColor
            window.navigationBarColor = backgroundColor
            container.setBackgroundColor(backgroundColor)
        } catch (_: Exception) {}
    }

    override fun onBackPressed() {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        recyclerAdapter.updateList(getAppList())
    }

    override fun onDestroy() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        super.onDestroy()
    }
}