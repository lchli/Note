
package com.lch

import android.app.Application
import android.os.Environment
import com.google.android.gms.ads.MobileAds
import com.lch.cl.Contexter
import com.lch.cl.FileScanner
import com.lch.cl.ad.AppOpenManager





class ClnApplication : Application() {
    private lateinit var appOpenManager: AppOpenManager

    override fun onCreate() {
        super.onCreate()
        Thread.currentThread().uncaughtExceptionHandler =
            Thread.UncaughtExceptionHandler { _, e -> e.printStackTrace() }

        Contexter.bindContext(this)

        FileScanner.scanBigFile(Environment.getExternalStorageDirectory())

        MobileAds.initialize(this) {}
        appOpenManager =  AppOpenManager(this);

    }

}
