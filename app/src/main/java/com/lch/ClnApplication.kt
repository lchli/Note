
package com.lch

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.os.Environment
import androidx.appcompat.app.AppCompatDelegate
import com.blankj.utilcode.util.SPUtils
import com.lch.cl.Contexter
import com.lch.cl.FileScanner
import com.lch.cl.SpKey
import com.lch.cl.UMUtil
import com.lch.cln.BuildConfig
import com.umeng.cconfig.RemoteConfigSettings
import com.umeng.cconfig.UMRemoteConfig
import com.umeng.commonsdk.UMConfigure


class ClnApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Thread.currentThread().setUncaughtExceptionHandler(object:Thread.UncaughtExceptionHandler{
            override fun uncaughtException(t: Thread, e: Throwable) {
                e.printStackTrace()
            }


        })
        Contexter.bindContext(this)

        val nightMode = if (SDK_INT >= Q) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        } else {
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)

        FileScanner.scanBigFile(Environment.getExternalStorageDirectory())

        UMRemoteConfig.getInstance().setConfigSettings(
            RemoteConfigSettings.Builder().setAutoUpdateModeEnabled(true).build()
        )

        UMConfigure.preInit(this,"6188923fe0f9bb492b50e6b7","official")
        UMConfigure.setLogEnabled(BuildConfig.DEBUG)

        //判断是否同意隐私协议，uminit为1时为已经同意，直接初始化umsdk
        if(SPUtils.getInstance().getBoolean(SpKey.is_proto_agreed,false)){
            UMUtil.init(this)
        }




    }

}
