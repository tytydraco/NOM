package com.draco.nom

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val launcherIntent = Intent(Intent.ACTION_MAIN, null)
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val activities = packageManager.queryIntentActivities(launcherIntent, 0)
        activities.sortBy {
            it.activityInfo.name
        }

        val appList = ArrayList<AppInfo>()
        for (app in activities) {
            val id = app.activityInfo.packageName

            if (id == BuildConfig.APPLICATION_ID)
                continue

            val info = AppInfo()
            info.id = id
            info.name = app.activityInfo.loadLabel(packageManager).toString()
            info.img = packageManager.getApplicationIcon(id)

            appList.add(info)
        }

        appList.sortBy {
            it.name.toLowerCase()
        }

        recycler = findViewById(R.id.recycler)
        recycler.setItemViewCacheSize(250)
        recycler.adapter = RecyclerAdapter(appList, recycler, packageManager)
        recycler.layoutManager = GridLayoutManager(this, 5)
        val statusSize = resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"))
        val navSize = resources.getDimensionPixelSize(resources.getIdentifier("navigation_bar_height", "dimen", "android"))
        recycler.setPadding(0, statusSize, 0, navSize)
    }
}