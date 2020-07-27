package com.draco.nom

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: RecyclerAdapter
    private lateinit var appInfoList: ArrayList<AppInfo>

    private fun getAppList(): ArrayList<AppInfo> {
        val launcherIntent = Intent(Intent.ACTION_MAIN, null)
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val activities = packageManager.queryIntentActivities(launcherIntent, 0)
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

        return appList
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler = findViewById(R.id.recycler)
        recycler.setItemViewCacheSize(250)

        appInfoList = getAppList()
        adapter = RecyclerAdapter(appInfoList, recycler, packageManager)
        recycler.adapter = adapter

        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val iconSize = resources.getDimension(R.dimen.icon_size) / displayMetrics.density
        val columns = (screenWidthDp / iconSize).toInt()
        recycler.layoutManager = GridLayoutManager(this, columns)

        val statusSize = resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"))
        val navSize = resources.getDimensionPixelSize(resources.getIdentifier("navigation_bar_height", "dimen", "android"))
        recycler.setPadding(0, statusSize, 0, navSize)
    }

    private fun refreshAppList() {
        val newAppInfoList = getAppList()
        if (appInfoList != newAppInfoList)
            adapter.updateList(newAppInfoList)
    }

    override fun onResume() {
        super.onResume()
        refreshAppList()
    }

    override fun onBackPressed() {
        refreshAppList()
    }
}