package com.draco.nom.views

import android.os.Bundle
import android.view.KeyEvent
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
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler = findViewById(R.id.recycler)

        window.decorView.setOnApplyWindowInsetsListener { _, windowInsets ->
            windowInsets
        }

        viewModel.packageIdNameMap.observe(this) {
            recyclerAdapter.appList = it!!
            recyclerAdapter.notifyDataSetChanged()
        }

        recyclerAdapter = LauncherRecyclerAdapter(this, emptyList()).apply {
            setHasStableIds(true)
        }

        recycler.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(this@LauncherActivity)
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