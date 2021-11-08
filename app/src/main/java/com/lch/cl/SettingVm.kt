package com.lch.cl

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.ToastUtils
import com.lch.cln.BuildConfig
import com.lch.cln.R


class SettingVm : BaseVm() {
    val liveVersion=MutableLiveData<String>()

    init {
        liveVersion.postValue("版本号：${BuildConfig.VERSION_NAME}")
    }

    fun checkUpdateClick(v:View){
        ToastUtils.showLong("已经是最新版本")
    }

    fun userProtoClick(v:View){
        ProtoActivity.start(v.context,"用户协议",v.resources.getString(R.string.user_proto_detail))
    }

    fun privacyProtoClick(v:View){
        ProtoActivity.start(v.context,"隐私政策",v.resources.getString(R.string.pri_proto_detail))
    }
}