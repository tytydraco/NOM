package com.draco.nom.recyclers.factories

import android.content.res.Resources
import android.widget.EdgeEffect
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.draco.nom.recyclers.LauncherRecyclerAdapter

class LauncherEdgeEffectFactory : RecyclerView.EdgeEffectFactory() {
    companion object {
        const val PHYSICS_PULL_MODIFIER = 0.1f

        /**
         * Ratio of how far the user has to drag down to trigger the event
         */
        const val PULL_DOWN_THRESHOLD = 0.5f
    }

    /**
     * Has the user successfully activated the pull down trigger?
     */
    private var pullDownActivated = false

    /**
     * Register what happens when we trigger the pull down
     */
    var pullDownListener: (() -> Unit)? = null

    /**
     * How far we have dragged
     */
    private var trueTranslationY = 0f

    /**
     * How far we have to drag to trigger the pull down
     */
    private var pullThreshold = 0


    /**
     * Since screen height changes on rotate, we need to manually update the threshold when
     * the screen size changes
     */
    fun updatePullThreshold(): Float {
        val height = Resources.getSystem().displayMetrics.heightPixels
        return height * PULL_DOWN_THRESHOLD
    }

    inner class RecyclerEdgeEffect(private val view: RecyclerView, direction: Int) : EdgeEffect(view.context) {
        /**
         * Apply a negative when dragging down
         */
        private val directionModifier = if (direction == DIRECTION_TOP) 1 else -1

        private fun handlePull(deltaDistance: Float) {
            val rawTranslationYDelta = directionModifier * view.height * deltaDistance
            val translationYDelta = rawTranslationYDelta * PHYSICS_PULL_MODIFIER

            trueTranslationY += rawTranslationYDelta

            for (child in view.children) {
                val holder = view.getChildViewHolder(child) as LauncherRecyclerAdapter.ViewHolder
                holder.translationY.cancel()
                holder.itemView.translationY += translationYDelta
            }

            /* Only trigger pull down once */
            if (!pullDownActivated && trueTranslationY >= pullThreshold)
                pullDownActivated = true
        }

        override fun onPull(deltaDistance: Float) {
            super.onPull(deltaDistance)
            handlePull(deltaDistance)
        }

        override fun onPull(deltaDistance: Float, displacement: Float) {
            super.onPull(deltaDistance, displacement)
            handlePull(deltaDistance)
        }

        override fun onRelease() {
            super.onRelease()

            /* Trigger our pull down action now and reset our pull trigger status */
            if (pullDownActivated) {
                pullDownListener?.invoke()
                pullDownActivated = false
            }

            /* We let go of the screen */
            trueTranslationY = 0f

            for (child in view.children) {
                val holder = view.getChildViewHolder(child) as LauncherRecyclerAdapter.ViewHolder
                holder.translationY.start()
            }
        }

        override fun onAbsorb(velocity: Int) {
            super.onAbsorb(velocity)
            val translationVelocity = directionModifier * velocity
            for (child in view.children) {
                val holder = view.getChildViewHolder(child) as LauncherRecyclerAdapter.ViewHolder
                holder.translationY
                    .setStartVelocity(translationVelocity.toFloat())
                    .start()
            }
        }
    }

    override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
        return RecyclerEdgeEffect(view, direction)
    }
}