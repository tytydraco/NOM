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
import androidx.lifecycle.viewModelScope
import com.draco.nom.R
import com.draco.nom.models.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.coroutineContext

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    private val _appList = MutableLiveData<Array<AppInfo>>()
    val appList: LiveData<Array<AppInfo>> = _appList

    init {
        updateList()
    }

    fun updateList() {
        viewModelScope.launch(Dispatchers.IO) {
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

            if (!_appList.value.contentEquals(newAppList.toTypedArray()))
                _appList.postValue(newAppList.toTypedArray())
        }
    }

    fun getColumns(displayMetrics: DisplayMetrics): Int {
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val iconSize = context.resources.getDimension(R.dimen.icon_size) / displayMetrics.density
        return (screenWidthDp / iconSize).toInt()
    }
}