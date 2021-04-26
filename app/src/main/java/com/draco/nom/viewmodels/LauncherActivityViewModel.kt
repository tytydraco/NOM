package com.draco.nom.viewmodels

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Build
import android.util.DisplayMetrics
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.draco.nom.BuildConfig
import com.draco.nom.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class LauncherActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val packageManager = application.applicationContext.packageManager

    private val _packageIdList = MutableLiveData<List<String>>()
    val packageIdList: LiveData<List<String>> = _packageIdList

    /**
     * Refresh local package id list
     */
    fun updatePackageIdList() {
        viewModelScope.launch(Dispatchers.IO) {
            /* Fetch all applications with a launcher intent */
            val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            /* Get all launcher activities sorted by name */
            val activities = packageManager.queryIntentActivities(launcherIntent, 0).sortedBy {
                it.loadLabel(packageManager).toString().toLowerCase(Locale.getDefault())
            }

            /* Add all package IDs to a new list */
            val newPackageIdList = mutableListOf<String>()
            for (app in activities) {
                val packageName = app.activityInfo.packageName
                if (packageName != BuildConfig.APPLICATION_ID)
                    newPackageIdList.add(packageName)
            }

            /* Push this change */
            if (_packageIdList.value != newPackageIdList)
                _packageIdList.postValue(newPackageIdList)
        }
    }

    /**
     * Determine how many columns a recycler should have to fit the icons
     */
    fun getColumns(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            activity.display!!.getRealMetrics(displayMetrics)
        else
            activity.windowManager.defaultDisplay.getRealMetrics(displayMetrics)

        val screenWidthDp = activity.resources.configuration.screenWidthDp
        val iconSize = activity.resources.getDimension(R.dimen.icon_size) / displayMetrics.density
        return (screenWidthDp / iconSize).toInt()
    }
}