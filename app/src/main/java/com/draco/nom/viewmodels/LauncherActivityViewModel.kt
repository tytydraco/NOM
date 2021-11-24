package com.draco.nom.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draco.nom.BuildConfig
import com.draco.nom.models.App
import com.draco.nom.recyclers.LauncherRecyclerAdapter
import com.draco.nom.recyclers.factories.LauncherEdgeEffectFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class LauncherActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val packageManager = application.applicationContext.packageManager
    private val inputMethodManager = application.applicationContext
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    private val _appList = MutableLiveData<List<App>>()
    val appList: LiveData<List<App>> = _appList

    private val _packageListProgress = MutableLiveData(0)
    val packageListProgress: LiveData<Int> = _packageListProgress

    /**
     * Recycler adapter that has stable ids used for showing apps
     */
    val recyclerAdapter = LauncherRecyclerAdapter(application.applicationContext, emptyList()).apply {
        setHasStableIds(true)
    }

    private val launcherEdgeEffectFactory = LauncherEdgeEffectFactory()

    /**
     * Prepare the recycler view
     */
    fun prepareRecycler(context: Context, recycler: RecyclerView) {
        recycler.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(context)
            edgeEffectFactory = launcherEdgeEffectFactory
            setHasFixedSize(true)
            setItemViewCacheSize(1000)
        }
    }

    /**
     * Hide the soft keyboard
     */
    fun hideSoftKeyboard(view: View) {
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

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
            _packageListProgress.postValue(0)
            for (activityIndex in activities.indices) {
                val activity = activities[activityIndex]
                val packageId = activity.activityInfo.packageName
                if (packageId != BuildConfig.APPLICATION_ID) {
                    val label = packageManager
                        .getApplicationInfo(packageId, 0)
                        .loadLabel(packageManager).toString()
                    val icon = packageManager.getApplicationIcon(packageId)

                    newAppList += App(packageId, label, icon)
                }
                val progress = ((activityIndex.toFloat() / activities.size) * 100).roundToInt()
                _packageListProgress.postValue(progress)
            }

            /* Sort this new map by application label */
            newAppList.sortBy { it.name.lowercase() }

            /* Push this change */
            if (_appList.value != newAppList)
                _appList.postValue(newAppList)
        }
    }
}