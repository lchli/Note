package com.lch.cl

import android.app.Activity
import android.view.View
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lch.cl.ad.InterAdUtil
import com.lch.cl.ad.RewardAdUtil
import com.lch.cl.util.ActivityScopeStore
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

    private fun deleteClick(view: View, filePath: String) {
        loading.postValue(true)

        if (FileScanner.del(filePath)) {
            ToastUtils.showLong("delete success")
            loading.postValue(false)

            backClick(view)
        } else {
            loading.postValue(false)

            ToastUtils.showLong("delete fail.")
        }


    }

    private fun loadDelAd(view: View, filePath: String) {
        if (context == null) {
            deleteClick(view, filePath)
            return
        }

        (ActivityScopeStore.of(MainActivity::class.java)[RewardAdUtil::class.java] as? RewardAdUtil)?.show(
            context!!
        ) {
            deleteClick(view, filePath)
        }
    }

    fun showDelDialog(v: View, filePath: String) {
        if (v.context !is Activity) {
            return
        }
        MaterialAlertDialogBuilder(v.context)
            .setTitle("alert")
            .setMessage("this file can not recovery after delete,continue?")
            .setNegativeButton("cancel") { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton("confirm") { dialog, which ->
                loadDelAd(v, filePath)
                dialog.dismiss()
            }
            .show()
    }

    fun backClick(view: View) {
        view.findNavController().navigateUp()
    }


}