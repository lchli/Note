package com.lch.cl

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File

class FileListVm : BaseVm(), Observer<Pair<Boolean, MutableList<String>>> {
    val state = MutableLiveData<List<String>>()
    val isFinished = MutableLiveData(false)
    private var finishedList: MutableList<String>? = null

    init {
        FileScanner.fileState.observeForever(this)
    }


    fun listenScan() {
        FileScanner.sendDataChanged()
    }

    fun refresh() {
        FileScanner.scanBigFile(Environment.getExternalStorageDirectory())
    }


    fun sort(type: FileSortType = FileSortType.Size(FileConst.SORT_DIRECTION_ASC)) {

        viewModelScope.launch {
            if (finishedList.isNullOrEmpty()) {
                return@launch
            }
            val list = mutableListOf<String>()
            list.addAll(finishedList!!)

            if (type is FileSortType.Size) {
                if (type.direction == FileConst.SORT_DIRECTION_ASC) {
                    list.sortBy {
                        File(it).length()
                    }
                } else {
                    list.sortByDescending {
                        File(it).length()
                    }
                }
            } else if (type is FileSortType.Time) {
                if (type.direction == FileConst.SORT_DIRECTION_ASC) {
                    list.sortBy {
                        File(it).lastModified()
                    }
                } else {
                    list.sortByDescending {
                        File(it).lastModified()
                    }
                }
            }

            state.postValue(list)

        }

    }

    fun filter(type: FileFilterType = FileFilterType.Size(10 * 1024 * 1024L)) {

        viewModelScope.launch {
            if (finishedList.isNullOrEmpty()) {
                return@launch
            }

            if (type is FileFilterType.Size) {
                finishedList!!.filter {
                    File(it).length() > type.length
                }.apply {
                    state.postValue(this)
                }

            } else if (type is FileFilterType.Category) {
                finishedList!!.filter {
                    File(it).name.endsWith("")
                }.apply {
                    state.postValue(this)
                }
            } else if (type is FileFilterType.Date) {
                finishedList!!.filter {
                    File(it).lastModified()>=type.start&&File(it).lastModified()<=type.end
                }.apply {
                    state.postValue(this)
                }
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
        if (it.first) {
            this.finishedList = it.second
            isFinished.postValue(true)
        }

        state.postValue(it.second)
    }


}