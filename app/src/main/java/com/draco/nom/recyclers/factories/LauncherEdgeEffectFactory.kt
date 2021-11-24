package com.draco.nom.recyclers.factories

import android.widget.EdgeEffect
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.draco.nom.recyclers.LauncherRecyclerAdapter

class LauncherEdgeEffectFactory : RecyclerView.EdgeEffectFactory() {
    companion object {
        const val PHYSICS_PULL_MODIFIER = 0.1f
    }

    inner class RecyclerEdgeEffect(private val view: RecyclerView, direction: Int) : EdgeEffect(view.context) {
        /**
         * Apply a negative when dragging down
         */
        private val directionModifier = if (direction == DIRECTION_TOP) 1 else -1

        private fun handlePull(deltaDistance: Float) {
            val rawTranslationYDelta = directionModifier * view.height * deltaDistance
            val translationYDelta = rawTranslationYDelta * PHYSICS_PULL_MODIFIER

            for (child in view.children) {
                val holder = view.getChildViewHolder(child) as LauncherRecyclerAdapter.ViewHolder
                holder.translationY.cancel()
                holder.itemView.translationY += translationYDelta
            }
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