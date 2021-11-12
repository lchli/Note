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
import com.lch.cl.util.log
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
        log("deleteClick")

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
        log("loadDelAd")
        if (context == null) {
            log("context == null")
            deleteClick(view, filePath)
            return
        }
        log("RewardAdUtil java name:${RewardAdUtil::class.java.name}")
        val mRewardAdUtil=LifecycleScopeObject.of(MainActivity::class.java).getData(RewardAdUtil::class.java.name) as? RewardAdUtil
        log("mRewardAdUtil:$mRewardAdUtil")

        mRewardAdUtil?.show(
            context!!
        ) {
            log("loadDelAd finish")
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
                log("cancel click")
                dialog.dismiss()
            }
            .setPositiveButton(getStrings(R.string.continues)) { dialog, which ->
                log("continue click")
                loadDelAd(v, filePath)
                dialog.dismiss()
            }
            .show()
    }

    fun backClick(view: View) {
        view.findNavController().navigateUp()
    }


}