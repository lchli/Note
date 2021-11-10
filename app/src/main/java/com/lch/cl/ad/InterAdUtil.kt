package com.lch.cl.ad

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class InterAdUtil(
    private val adId: String = AdIds.inter_ad
) {
    private var mInterstitialAd: InterstitialAd? = null
    private val TAG = "InterAdUtil"

    fun show(activityContext: Activity) {
        try {
            if (mInterstitialAd == null) {
                loadAd(activityContext)
                return
            }
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    mInterstitialAd = null
                }
            }
            mInterstitialAd?.show(activityContext)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadAd(activityContext: Activity) {
        try {
            val adRequest = AdRequest.Builder().build()

            InterstitialAd.load(
                activityContext,
                adId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(TAG, adError?.message)

                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        mInterstitialAd = interstitialAd
                    }
                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}