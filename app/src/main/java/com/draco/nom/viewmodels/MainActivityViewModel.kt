package com.draco.nom.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.hardware.display.DisplayManager
import android.util.DisplayMetrics
import android.util.Log
import androidx.core.hardware.display.DisplayManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.draco.nom.R
import com.draco.nom.models.AppInfo
import java.util.*

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    private val appList = MutableLiveData<Array<AppInfo>>()
    fun getAppList(): LiveData<Array<AppInfo>> = appList

    init {
        updateList()
    }

    fun updateList() {
        val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val activities = context.packageManager.queryIntentActivities(launcherIntent, 0)
        val newAppList = arrayListOf<AppInfo>()

        for (app in activities) {
            if (app.activityInfo.packageName == context.packageName)
                continue

            newAppList.add(
                AppInfo(
                    app.activityInfo.loadLabel(context.packageManager).toString(),
                    app.activityInfo.packageName
                )
            )
        }

        newAppList.sortBy {
            it.label.toLowerCase(Locale.getDefault())
        }

        if (!appList.value.contentEquals(newAppList.toTypedArray()))
            appList.value = newAppList.toTypedArray()
    }

    fun getColumns(displayMetrics: DisplayMetrics): Int {
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val iconSize = context.resources.getDimension(R.dimen.icon_size) / displayMetrics.density
        return (screenWidthDp / iconSize).toInt()
    }
}