package com.draco.nom.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draco.nom.R
import com.draco.nom.recyclers.LauncherRecyclerAdapter
import com.draco.nom.recyclers.RecyclerEdgeEffectFactory
import com.draco.nom.viewmodels.MainActivityViewModel

class MainActivity: AppCompatActivity() {
    private lateinit var viewModel: MainActivityViewModel

    private lateinit var recyclerAdapter: LauncherRecyclerAdapter
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        recycler = findViewById(R.id.recycler)

        setupRecyclerView()

        viewModel.appList.observe(this) {
            recyclerAdapter.appList = viewModel.appList.value!!
            recyclerAdapter.notifyDataSetChanged()
        }
    }

    private fun setupRecyclerView() {
        recyclerAdapter = LauncherRecyclerAdapter(this, viewModel.appList.value!!).apply {
            setHasStableIds(true)
        }

        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val iconSize = resources.getDimension(R.dimen.icon_size) / displayMetrics.density

        val columns = (screenWidthDp / iconSize).toInt()

        with (recycler) {
            adapter = recyclerAdapter
            layoutManager = GridLayoutManager(context, columns)
            edgeEffectFactory = RecyclerEdgeEffectFactory()

            setItemViewCacheSize(1000)
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.updateList())
            recyclerAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {}
}