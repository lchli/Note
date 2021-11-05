package com.lch.cl

import androidx.lifecycle.MutableLiveData

class FileDetailVm:BaseVm() {
    val state=MutableLiveData<FileDetailUiState>()

    fun load(path:String?){

    }

}