
package com.lch

import android.app.Application
import android.os.Environment
import com.google.android.gms.ads.MobileAds
import com.lch.cl.Contexter
import com.lch.cl.FileScanner
import com.lch.cl.ad.AppOpenManager
import com.lch.cl.uniad.AppOpenManagerUni
import com.lch.cl.util.AppLifeCycle
import io.dcloud.adnative.UniAdManager


class ClnApplication : Application() {
    private lateinit var appOpenManager: AppOpenManagerUni

    override fun onCreate() {
        super.onCreate()
        Thread.currentThread().uncaughtExceptionHandler =
            Thread.UncaughtExceptionHandler { _, e -> e.printStackTrace() }

        Contexter.bindContext(this)
        registerActivityLifecycleCallbacks(AppLifeCycle())

        FileScanner.scanBigFile(Environment.getExternalStorageDirectory())

        MobileAds.initialize(this) {}
        appOpenManager =  AppOpenManagerUni(this);

        val defaultConfig = UniAdManager.obtainDefaultConfig("NA8CFB058", "121861270411")
        UniAdManager.init(this, defaultConfig)

    }

}
