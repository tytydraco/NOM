package com.draco.nom.views

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draco.nom.R
import com.draco.nom.recyclers.LauncherRecyclerAdapter
import com.draco.nom.recyclers.RecyclerEdgeEffectFactory
import com.draco.nom.viewmodels.MainActivityViewModel

class MainActivity: AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var recyclerAdapter: LauncherRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.appList.observe(this) {
            recyclerAdapter.appList = viewModel.appList.value!!
            recyclerAdapter.notifyDataSetChanged()
        }

        recyclerAdapter = LauncherRecyclerAdapter(this, emptyArray()).apply {
            setHasStableIds(true)
        }

        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            display!!.getRealMetrics(displayMetrics)
        else
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)

        val columns = viewModel.getColumns(displayMetrics)
        findViewById<RecyclerView>(R.id.recycler).apply {
            adapter = recyclerAdapter
            layoutManager = GridLayoutManager(this@MainActivity, columns)
            edgeEffectFactory = RecyclerEdgeEffectFactory()
            setItemViewCacheSize(1000)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateList()
    }

    override fun onBackPressed() {}
}