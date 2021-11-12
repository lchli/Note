package com.lch.cl

import android.app.Activity
import android.view.View
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lch.cl.ad.RewardAdUtil
import com.lch.cl.util.LifecycleScopeObject
import com.lch.cl.util.getStrings
import com.lch.cln.R
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
            ToastUtils.showLong(R.string.del_success)
            loading.postValue(false)

            backClick(view)
        } else {
            loading.postValue(false)

            ToastUtils.showLong(R.string.del_fail)
        }


    }

    private fun loadDelAd(view: View, filePath: String) {
        if (context == null) {
            deleteClick(view, filePath)
            return
        }

        (LifecycleScopeObject.of(MainActivity::class.java).getData(RewardAdUtil::class.java.name) as? RewardAdUtil)?.show(
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
            .setTitle(getStrings(R.string.alert))
            .setMessage(getStrings(R.string.cannot_recovery))
            .setNegativeButton(getStrings(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(getStrings(R.string.continues)) { dialog, which ->
                loadDelAd(v, filePath)
                dialog.dismiss()
            }
            .show()
    }

    fun backClick(view: View) {
        view.findNavController().navigateUp()
    }


}