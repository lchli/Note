package com.lch.cl

import android.content.Context
import android.content.res.Resources
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.materialstudies.owl.R

class FileListView(context: Context) : FrameLayout(context), Observer<Pair<Boolean,MutableList<String>>> {
    private val mFileListAdapter = FileListAdapter()
    private lateinit var mRecyclerView: RecyclerView

    init {
        val view = View.inflate(context, R.layout.file_list_view, this)
        view.apply {
            mRecyclerView = findViewById<RecyclerView>(R.id.file_list_view)
            val statusBar = findViewById<View>(R.id.statusBar)
            statusBar.apply {
                layoutParams.height = getStatusBarHeight()
            }
            mRecyclerView.adapter = mFileListAdapter
        }

    }

    fun getStatusBarHeight(): Int {
        val resources: Resources = getResources()
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    fun start() {
        FileScanner.fileState.observeForever(this)
        FileScanner.scanBigFile(Environment.getExternalStorageDirectory())
    }


    fun stop() {
        FileScanner.fileState.removeObserver(this)
        FileScanner.forceStop()
    }

    override fun onChanged(it: Pair<Boolean,MutableList<String>>) {
        Log.e("sss", "onChanged")
        //mFileListAdapter.submitList(it)


        mRecyclerView.post { mRecyclerView.scrollToPosition(0) }


    }


}