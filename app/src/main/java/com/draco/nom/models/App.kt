package com.draco.nom.models

import android.graphics.drawable.Drawable

data class App(
    val id: String,
    val name: String,
    val icon: Drawable,
) {
    override fun equals(other: Any?): Boolean {
        if (other !is App)
            return false

        /* Intentionally ignore icons when comparing */
        return id == other.id && name == other.name
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}