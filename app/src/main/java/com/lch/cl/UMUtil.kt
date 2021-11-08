package com.lch.cl

import android.content.Context
import com.umeng.cconfig.UMRemoteConfig
import com.umeng.commonsdk.UMConfigure

object UMUtil {
     fun init(context: Context){
         UMConfigure.init(context,"6188923fe0f9bb492b50e6b7","official",UMConfigure.DEVICE_TYPE_PHONE, "")
     }


}