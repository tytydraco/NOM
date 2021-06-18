package com.draco.nom.views

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.draco.nom.R
import com.draco.nom.recyclers.LauncherRecyclerAdapter
import com.draco.nom.recyclers.factories.LauncherEdgeEffectFactory
import com.draco.nom.viewmodels.LauncherActivityViewModel

class LauncherActivity: AppCompatActivity() {
    private val viewModel: LauncherActivityViewModel by viewModels()
    private lateinit var recyclerAdapter: LauncherRecyclerAdapter

    private lateinit var recycler: RecyclerView
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler = findViewById(R.id.recycler)
        progress = findViewById(R.id.progress)

        window.decorView.setOnApplyWindowInsetsListener { _, windowInsets ->
            windowInsets
        }

        viewModel.packageIdNameMap.observe(this) {
            if (it != null) {
                progress.visibility = View.GONE
                recyclerAdapter.appList = it
                recyclerAdapter.notifyDataSetChanged()
            }
        }

        viewModel.packageListProgress.observe(this) {
            progress.progress = it
        }

        recyclerAdapter = LauncherRecyclerAdapter(this, emptyList()).apply {
            setHasStableIds(true)
        }

        recycler.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(this@LauncherActivity)
            edgeEffectFactory = LauncherEdgeEffectFactory()
            setHasFixedSize(true)
            setItemViewCacheSize(1000)
            layoutManager!!.isItemPrefetchEnabled = true
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (viewModel.handleKeyboardNavEvent(event, recycler))
            true
        else
            super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        viewModel.updatePackageIdList()
    }

    override fun onBackPressed() {}
}