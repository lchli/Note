package com.lch.cl

import android.content.Context
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File

class FileListVm:BaseVm(), Observer<Pair<Boolean,MutableList<String>>> {
    val state = MutableLiveData<MutableList<String>>()
    val isFinished = MutableLiveData<Boolean>(false)
    private var finishedList: MutableList<String>? = null

    init {
        FileScanner.fileState.observeForever(this)
    }




    fun startScan(){
        FileScanner.scanBigFile(Environment.getExternalStorageDirectory())
    }

    fun sort(type:FileSortType=FileSortType.Size(FileConst.SORT_DIRECTION_ASC)){

        viewModelScope.launch {
            if(finishedList.isNullOrEmpty()){
                return@launch
            }
            val list= mutableListOf<String>()
            list.addAll(finishedList!!)

            if(type is FileSortType.Size) {
                if(type.direction==FileConst.SORT_DIRECTION_ASC) {
                    list.sortBy {
                        File(it).length()
                    }
                }else{
                    list.sortByDescending {
                        File(it).length()
                    }
                }
            }else if(type is FileSortType.Time){
                if(type.direction==FileConst.SORT_DIRECTION_ASC) {
                    list.sortBy {
                        File(it).lastModified()
                    }
                }else{
                    list.sortByDescending {
                        File(it).lastModified()
                    }
                }
            }

            state.postValue(list)

        }

    }

    fun filter(type:FileFilterType=FileFilterType.Size(10*1024*1024L)){

        viewModelScope.launch {
            if(finishedList.isNullOrEmpty()){
                return@launch
            }
            val list= mutableListOf<String>()
            list.addAll(finishedList!!)

            if(type is FileFilterType.Size) {
                list.filter {
                    File(it).length()>type.length
                }

            }else if(type is FileFilterType.Category){
                list.filter {
                    File(it).name.endsWith("")
                }
            }

            state.postValue(list)

        }

    }


    override fun onCleared() {
        super.onCleared()
        FileScanner.fileState.removeObserver(this)
        FileScanner.forceStop()
    }

    override fun onChanged(it: Pair<Boolean, MutableList<String>>?) {
        if(it==null){
            return
        }
        if(it.first){
            this.finishedList=it.second
            isFinished.postValue(true)
        }

        state.postValue(it.second)
    }


}