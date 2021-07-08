package com.draco.nom.utils

import com.draco.nom.models.App
import java.util.*

class AppListSearcher {
    private var currentSearchQuery: Deque<String> = ArrayDeque()

    /**
     * Narrow our search with another search term
     */
    fun narrow(term: String) {
        currentSearchQuery.add(term)
    }

    /**
     * Pop the last added search term
     */
    fun broaden() {
        currentSearchQuery.pollLast()
    }

    /**
     * Reset our search terms to a clean slate
     */
    fun reset() {
        currentSearchQuery.clear()
    }

    /**
     * Return the first App found by our search query
     */
    fun evaluate(list: List<App>): Int {
        val searchString = currentSearchQuery.joinToString("")
        return list.indexOfFirst {
            it.name.lowercase().startsWith(searchString)
        }
    }
}