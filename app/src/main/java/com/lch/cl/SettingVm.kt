package com.lch.cl

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.ToastUtils
import com.lch.cln.BuildConfig
import com.lch.cln.R


class SettingVm : BaseVm() {
    val liveVersion=MutableLiveData<String>()

    fun init(){
        liveVersion.postValue(context?.getString(R.string.app_version,BuildConfig.VERSION_NAME))
    }

    fun checkUpdateClick(v:View){
        ToastUtils.showLong(R.string.app_latest)
    }

}