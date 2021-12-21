package com.lch.cl

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ScreenUtils
import com.lch.cl.util.log
import io.dcloud.adnative.UniAdManager
import io.dcloud.adnative.UniAdManager.SplashOption
import io.dcloud.adnative.model.ISplashADListener
import io.dcloud.adnative.util.DoToast
import io.dcloud.adnative.util.Logger

class Launcher: AppCompatActivity() {

    companion object{
        fun start(activity: Activity){
            Intent(activity,Launcher::class.java).apply {
                activity.startActivity(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadSplashAD(ScreenUtils.getAppScreenWidth(),ScreenUtils.getAppScreenHeight(),
            FrameLayout(this)
        )

    }


    private fun gotoMainActivity(){
        startActivity(Intent(this,MainActivity::class.java))
    }

    /**
     * 开始splash 广告
     * @param width 宽度
     * @param height 高度
     * @param parent
     */
    private fun loadSplashAD(width: Int, height: Int, parent: ViewGroup) {
        val splashOption = SplashOption()
        splashOption.expectPXWidth = width
        splashOption.expectPXHeight = height
        UniAdManager.showSplashAD(splashOption, parent, object : ISplashADListener {
            override fun onLoadError(code: Int, message: String) {
                log("onLoadError:$code,$message")
                gotoMainActivity()
                finish()
            }

            override fun onLoadSuccess() {
                log("onLoadSuccess")
            }

            override fun onADClick(view: View, type: Int) {
                log("onADClick")
            }

            override fun onAdShow(view: View, type: Int) {
                log("onAdShow")
            }

            override fun onAdTimeOver() {
                log("onAdTimeOver")
                gotoMainActivity()
                finish()
            }

            override fun onDownloadActive(
                totalBytes: Long,
                currBytes: Long,
                fileName: String,
                appName: String
            ) {
                Logger.d("onDownloadActive  ---  $currBytes")
            }

            override fun onDownloadFailed(
                totalBytes: Long,
                currBytes: Long,
                fileName: String,
                appName: String
            ) {
                Logger.d("onDownloadFailed  ---  $currBytes")
            }

            override fun onDownloadFinished(totalBytes: Long, fileName: String, appName: String) {
                Logger.d("onDownloadFinished  ---  $fileName")
            }

            override fun onInstalled(fileName: String, appName: String) {
                Logger.d("onInstalled  ---  $fileName")
            }
        })
    }
}