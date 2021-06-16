package com.draco.nom.recyclers.scrollers

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

/**
 * Smoothly scroll to the start of the target view and request focus
 */
class SmoothScrollerTopAndFocus(context: Context) : LinearSmoothScroller(context) {
    override fun getVerticalSnapPreference(): Int = SNAP_TO_START
    override fun onTargetFound(
        targetView: View,
        state: RecyclerView.State,
        action: Action
    ) {
        targetView.requestFocus()
        super.onTargetFound(targetView, state, action)
    }
}