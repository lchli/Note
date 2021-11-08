package com.lch.cl

import com.umeng.cconfig.UMRemoteConfig

object AdUtil {

    fun isAdOpen():Boolean{
        return UMRemoteConfig.getInstance().getConfigValue("asw")=="1"
    }
}