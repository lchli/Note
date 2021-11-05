package com.lch.cl

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FileDetailVm : BaseVm() {
    val state = MutableLiveData<FileDetailUiState>()

    fun load(path: String?) {
        viewModelScope.launch {
            if (path != null) {
                state.value = FileDetailUiState(path, this@FileDetailVm)
            }
        }
    }

}