package com.lch.cl

import android.os.Build
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.blankj.utilcode.util.ToastUtils
import com.materialstudies.owl.BuildConfig
import com.materialstudies.owl.R
import com.materialstudies.owl.ui.mycourses.MyCoursesFragmentDirections
import com.materialstudies.owl.ui.search.SearchFragmentDirections

class SettingVm : BaseVm() {
    val liveVersion=MutableLiveData<String>()

    init {
        liveVersion.postValue("版本号：${BuildConfig.VERSION_NAME}")
    }

    fun checkUpdateClick(v:View){
        ToastUtils.showLong("已经是最新版本")
    }

    fun userProtoClick(v:View){

        val action = SearchFragmentDirections.actionSettingToProto("用户协议",v.resources.getString(R.string.user_proto))
        v.findNavController().navigate(action)
    }

    fun privacyProtoClick(v:View){

        val action = SearchFragmentDirections.actionSettingToProto("隐私政策",v.resources.getString(R.string.pri_proto))
        v.findNavController().navigate(action)
    }
}