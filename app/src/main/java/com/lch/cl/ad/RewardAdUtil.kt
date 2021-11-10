package com.lch.cl.ad

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardAdUtil(
    private val adId: String = AdIds.reward_ad
) {

    private var rewardedAd: RewardedAd? = null

    fun show(activityContext: Activity, cb: (() -> Unit)?) {
        try {
            val ad = rewardedAd
            if (ad == null) {
                loadAd(activityContext)
                cb?.invoke()
                return
            }

            val adCallback = OnUserEarnedRewardListener { // User earned reward.
                gotoNext(cb)
                loadAd(activityContext)
            }

            ad.show(activityContext, adCallback)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun gotoNext(cb: (() -> Unit)?) {
        cb?.invoke()
    }

    private fun loadAd(activityContext: Activity) {
        try {
            val adLoadCallback = object : RewardedAdLoadCallback() {

                override fun onAdLoaded(p0: RewardedAd) {
                    super.onAdLoaded(p0)
                    rewardedAd = p0
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    rewardedAd = null
                }
            }

            RewardedAd.load(activityContext, adId, AdRequest.Builder().build(), adLoadCallback)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}