package com.draco.nom.viewmodels

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.draco.nom.BuildConfig
import com.draco.nom.models.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LauncherActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val packageManager = application.applicationContext.packageManager

    private val _packageIdNameMap = MutableLiveData<List<App>>()
    val packageIdNameMap: LiveData<List<App>> = _packageIdNameMap

    /**
     * Refresh local package id list
     */
    fun updatePackageIdList() {
        viewModelScope.launch(Dispatchers.IO) {
            /* Fetch all applications with a launcher intent */
            val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            /* Get all launcher activities */
            val activities = packageManager.queryIntentActivities(launcherIntent, 0)

            /* Add all package IDs to a new list */
            val newAppList = mutableListOf<App>()
            for (activity in activities) {
                val packageId = activity.activityInfo.packageName
                if (packageId != BuildConfig.APPLICATION_ID) {
                    newAppList += App(
                        packageId,
                        packageManager
                            .getApplicationInfo(packageId, 0)
                            .loadLabel(packageManager).toString(),
                        packageManager.getApplicationIcon(packageId)
                    )
                }
            }

            /* Sort this new map by application label */
            newAppList.sortBy { it.name.lowercase() }

            /* Push this change */
            if (_packageIdNameMap.value != newAppList)
                _packageIdNameMap.postValue(newAppList)
        }
    }
}