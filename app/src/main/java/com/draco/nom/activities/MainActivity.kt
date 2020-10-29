package com.draco.nom.activities

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draco.nom.R
import com.draco.nom.recyclers.RecyclerAdapter
import com.draco.nom.recyclers.RecyclerEdgeEffectFactory
import com.draco.nom.utils.AppInfo
import java.util.*
import kotlin.collections.ArrayList

class MainActivity: AppCompatActivity() {
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var sharedPrefs: SharedPreferences

    private fun getAppList(): Array<AppInfo> {
        val launcherIntent = Intent(Intent.ACTION_MAIN, null)
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val activities = packageManager.queryIntentActivities(launcherIntent, 0)
        val appList = ArrayList<AppInfo>()

        for (app in activities) {
            val appId = app.activityInfo.packageName
            if (appId == packageName)
                continue

            val info = AppInfo(
                app.activityInfo.loadLabel(packageManager).toString(),
                appId
            )

            appList.add(info)
        }

        appList.sortBy {
            it.label.toLowerCase(Locale.getDefault())
        }

        return appList.toTypedArray()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val container = findViewById<LinearLayout>(R.id.container)
        val recycler = findViewById<RecyclerView>(R.id.recycler)

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        recyclerAdapter = RecyclerAdapter(getAppList(), recycler)
        recyclerAdapter.setHasStableIds(true)

        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val iconSize = resources.getDimension(R.dimen.icon_size) / displayMetrics.density
        val columns = 5.coerceAtLeast((screenWidthDp / iconSize).toInt())

        with (recycler) {
            setItemViewCacheSize(1000)
            adapter = recyclerAdapter
            layoutManager = GridLayoutManager(context, columns)
            edgeEffectFactory = RecyclerEdgeEffectFactory()
        }

        if (!sharedPrefs.getBoolean(getString(R.string.pref_rotation), true))
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val backgroundColorString = sharedPrefs.getString(getString(R.string.pref_background_color), "#00000000")
        try {
            val backgroundColor = Color.parseColor(backgroundColorString)
            window.statusBarColor = backgroundColor
            window.navigationBarColor = backgroundColor
            container.setBackgroundColor(backgroundColor)
        } catch (_: Exception) {}
    }

    override fun onBackPressed() {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
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