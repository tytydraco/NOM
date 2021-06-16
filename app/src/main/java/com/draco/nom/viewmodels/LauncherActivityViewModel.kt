package com.draco.nom.viewmodels

import android.app.Application
import android.content.Intent
import android.view.KeyEvent
import androidx.core.view.size
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draco.nom.BuildConfig
import com.draco.nom.models.App
import com.draco.nom.recyclers.LauncherRecyclerAdapter
import com.draco.nom.recyclers.scrollers.SmoothScrollerTopAndFocus
import com.draco.nom.repositories.constants.ScrollDirection
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

    /**
     * Scroll recycler to app that starts with the letter inputted by the user
     */
    private fun scrollToAppStartingWith(recycler: RecyclerView, starting: String) {
        val adapter = recycler.adapter as LauncherRecyclerAdapter
        val position = adapter.appList.indexOfFirst {
            it.name.lowercase().startsWith(starting)
        }

        if (position == -1)
            return

        val context = getApplication<Application>().applicationContext
        val layoutManager = recycler.layoutManager as LinearLayoutManager
        val scroller = SmoothScrollerTopAndFocus(context).apply {
            targetPosition = position
        }

        layoutManager.startSmoothScroll(scroller)
    }

    /**
     * Scroll recycler by an entire page
     */
    private fun pageScroll(recycler: RecyclerView, direction: ScrollDirection) {
        val context = getApplication<Application>().applicationContext
        val adapter = recycler.adapter as LauncherRecyclerAdapter
        val layoutManager = recycler.layoutManager as LinearLayoutManager
        val newPosition = when (direction) {
            ScrollDirection.UP -> {
                val topPos = layoutManager.findFirstVisibleItemPosition()
                val bottomPos = layoutManager.findLastVisibleItemPosition()
                val totalVisibleItems = bottomPos - topPos
                val newPos = topPos - totalVisibleItems

                newPos.coerceAtLeast(0)
            }
            ScrollDirection.DOWN -> {
                val bottomPos = layoutManager.findLastVisibleItemPosition()
                val lastItemPosition = adapter.itemCount - 1
                bottomPos.coerceAtMost(lastItemPosition)
            }
        }
        val scroller = SmoothScrollerTopAndFocus(context).apply {
            targetPosition = newPosition
        }

        layoutManager.startSmoothScroll(scroller)
    }

    /**
     * Attempt to handle user keyboard navigation events
     * @return True if the event was handled
     */
    fun handleKeyboardNavEvent(event: KeyEvent, recycler: RecyclerView): Boolean {
        val char = event.displayLabel

        if (isCharAlphanumeric(char)) {
            val letter = char.toString().lowercase()
            scrollToAppStartingWith(recycler, letter)
            return true
        }

        when (event.keyCode) {
            KeyEvent.KEYCODE_PAGE_UP -> {
                pageScroll(recycler, ScrollDirection.UP)
                return true
            }

            KeyEvent.KEYCODE_PAGE_DOWN -> {
                pageScroll(recycler, ScrollDirection.DOWN)
                return true
            }
        }

        return false
    }

    /**
     * Check if a character is alphanumeric
     */
    fun isCharAlphanumeric(char: Char): Boolean = char.toString().matches(Regex("[a-zA-Z0-9]+"))
}