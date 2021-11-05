package com.lch.cl

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class FileListVm : BaseVm(), Observer<Pair<Boolean, MutableList<String>>> {
    val state = MutableLiveData<Pair<Boolean, List<String>>>()
    val isFinished = MutableLiveData(false)
    private var finishedList: List<String>? = null
    private var preSort: FileSortType? = null
    private var preFilter: FileFilterType? = null

    init {
        FileScanner.fileState.observeForever(this)
    }


    fun listenScan() {
        FileScanner.sendDataChanged()
    }

    fun refresh() {
        preSort=null
        preFilter=null
        FileScanner.scanBigFile(Environment.getExternalStorageDirectory())
    }


    fun sort(type: FileSortType = FileSortType.Size(FileConst.SORT_DIRECTION_ASC)) {

        viewModelScope.launch(Dispatchers.IO) {
            if (finishedList.isNullOrEmpty()) {
                return@launch
            }
            val retlist = mutableListOf<String>()

            if (preFilter != null) {
                finishedList!!.filter {
                    preFilter!!.isMatch(File(it))
                }.apply { retlist.addAll(this) }
            } else {
                retlist.addAll(finishedList!!)
            }

            type.sort(retlist)

            preSort=type

            state.postValue(Pair(true, retlist))

        }

    }

    fun filter(type: FileFilterType = FileFilterType.Size(10 * 1024 * 1024L)) {

        viewModelScope.launch(Dispatchers.IO) {
            if (finishedList.isNullOrEmpty()) {
                return@launch
            }
            finishedList!!.filter {
                type.isMatch(File(it))
            }.apply {
                val ml=this.toMutableList()
                if(preSort!=null){
                    preSort!!.sort(ml)
                }

                preFilter=type

                state.postValue(Pair(true, ml))
            }

        }

    }


    override fun onCleared() {
        super.onCleared()
        FileScanner.fileState.removeObserver(this)
    }

    override fun onChanged(it: Pair<Boolean, MutableList<String>>?) {
        if (it == null) {
            return
        }
        if (it.first) {//finished
            this.finishedList = it.second
            isFinished.postValue(true)
        }
        var retlist=it.second

        preFilter?.apply {
            retlist= retlist.filter {
                isMatch(File(it))
            } as MutableList<String>
        }
        preSort?.sort(retlist)

        state.postValue(Pair(false, retlist))
    }


}