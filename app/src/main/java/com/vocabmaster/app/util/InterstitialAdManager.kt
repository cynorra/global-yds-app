package com.vocabmaster.app.util

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.vocabmaster.app.data.billing.BillingManager
import com.vocabmaster.app.data.remote.RemoteConfigManager

/**
 * Shows an interstitial at natural break points (currently: leaving a finished exam). Never
 * shown to Premium users, and can be killed entirely from Firebase Remote Config
 * (interstitial_ad_enabled) without an app release.
 */
object InterstitialAdManager {
    private const val AD_UNIT_ID = "ca-app-pub-4674211063760769/8929560181"
    private const val TAG = "InterstitialAdManager"

    private var interstitialAd: InterstitialAd? = null

    fun preload(context: Context) {
        if (!isEligible()) return
        if (interstitialAd != null) return

        InterstitialAd.load(
            context,
            AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.w(TAG, "Interstitial failed to load: ${adError.message}")
                    interstitialAd = null
                }
            }
        )
    }

    /** Shows the preloaded ad if one is ready and the user is still eligible; always calls
     * [onDone] exactly once, whether or not an ad was actually shown. */
    fun showIfReady(activity: Activity, onDone: () -> Unit) {
        val ad = interstitialAd
        if (ad == null || !isEligible()) {
            onDone()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                onDone()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
                onDone()
            }
        }
        ad.show(activity)
    }

    private fun isEligible(): Boolean {
        val isPremium = BillingManager.isPremium.value ?: false
        return !isPremium && RemoteConfigManager.isInterstitialAdEnabled
    }
}
