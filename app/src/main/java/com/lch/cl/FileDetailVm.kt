package com.lch.cl

import android.app.Activity
import android.view.View
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FileDetailVm : BaseVm() {
    val state = MutableLiveData<FileDetailUiState>()
    val loading = MutableLiveData(false)

    fun load(path: String?) {
        viewModelScope.launch {
            if (path != null) {
                state.value = FileDetailUiState(path, this@FileDetailVm)
            }
        }
    }

     private fun deleteClick(view: View,filePath:String) {
         loading.postValue(true)

         if(FileScanner.del(filePath)){
             ToastUtils.showLong("删除成功")
             loading.postValue(false)

             backClick(view)
         }else{
             loading.postValue(false)

             ToastUtils.showLong("删除失败")
         }


     }

    fun showDelDialog(v:View,filePath:String){
        if(v.context !is Activity){
            return
        }
        MaterialAlertDialogBuilder(v.context)
            .setTitle("提示")
            .setMessage("文件删除后将无法恢复，是否继续？")
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("确定") { dialog, which ->
                deleteClick(v,filePath)
                dialog.dismiss()
            }
            .show()
    }

    fun backClick(view: View) {
        view.findNavController().navigateUp()
    }


}