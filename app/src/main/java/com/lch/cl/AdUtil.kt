package com.lch.cl

import com.lch.cl.util.log
import com.umeng.cconfig.UMRemoteConfig

object AdUtil {

    fun isAdOpen():Boolean{
        val asw=UMRemoteConfig.getInstance().getConfigValue("asw")
        log("asw:$asw")
        return asw=="1"
    }
}