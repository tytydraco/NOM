package com.draco.nom.utils

import com.draco.nom.models.App
import java.util.*

class AppListSearcher {
    private var currentSearchQuery: Deque<String> = ArrayDeque()

    fun narrow(term: String) {
        currentSearchQuery.add(term)
    }

    fun broaden() {
        currentSearchQuery.pollLast()
    }

    fun reset() {
        currentSearchQuery.clear()
    }

    fun evaluate(list: List<App>): Int {
        val searchString = currentSearchQuery.joinToString("")
        return list.indexOfFirst {
            it.name.lowercase().startsWith(searchString)
        }
    }
}