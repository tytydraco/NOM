package com.draco.nom.views

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draco.nom.R
import com.draco.nom.recyclers.LauncherRecyclerAdapter
import com.draco.nom.viewmodels.LauncherActivityViewModel

class LauncherActivity: AppCompatActivity() {
    private val viewModel: LauncherActivityViewModel by viewModels()
    private lateinit var recyclerAdapter: LauncherRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.setOnApplyWindowInsetsListener { _, windowInsets ->
            windowInsets
        }

        viewModel.packageIdList.observe(this) {
            recyclerAdapter.packageIdList = viewModel.packageIdList.value!!
            recyclerAdapter.notifyDataSetChanged()
        }

        recyclerAdapter = LauncherRecyclerAdapter(this, emptyList()).apply {
            setHasStableIds(true)
        }

        findViewById<RecyclerView>(R.id.recycler).apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(this@LauncherActivity)
            setHasFixedSize(true)
            setItemViewCacheSize(1000)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updatePackageIdList()
    }

    override fun onBackPressed() {}
}