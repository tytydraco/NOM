package com.draco.nom.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draco.nom.R
import com.draco.nom.recyclers.LauncherRecyclerAdapter
import com.draco.nom.recyclers.RecyclerEdgeEffectFactory

class MainActivity: AppCompatActivity() {
    private lateinit var recyclerAdapter: LauncherRecyclerAdapter
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler = findViewById(R.id.recycler)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerAdapter = LauncherRecyclerAdapter(this).apply {
            setHasStableIds(true)
        }

        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val iconSize = resources.getDimension(R.dimen.icon_size) / displayMetrics.density
        val columns = 5.coerceAtLeast((screenWidthDp / iconSize).toInt())

        with (recycler) {
            adapter = recyclerAdapter
            layoutManager = GridLayoutManager(context, columns)
            edgeEffectFactory = RecyclerEdgeEffectFactory()

            setItemViewCacheSize(1000)
        }
    }

    override fun onResume() {
        super.onResume()
        recyclerAdapter.updateList()
    }

    override fun onBackPressed() {}
}