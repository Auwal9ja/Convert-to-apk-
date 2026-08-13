package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

object AdConstants {
    // User AdMob Account IDs
    const val APP_ID = "ca-app-pub-3975025431204010~3916645921"
    const val REWARDED_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3975025431204010/3201692020"
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-3975025431204010/9691716786"

    // Fallback Sample Test Ad Unit IDs
    const val SAMPLE_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val SAMPLE_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val SAMPLE_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    const val SAMPLE_REWARDED_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/5354046379"
}

/**
 * Reusable Compose Banner Ad component using Google Mobile Ads SDK.
 */
@Composable
fun BannerAd(
    modifier: Modifier = Modifier,
    adUnitId: String = AdConstants.SAMPLE_BANNER_AD_UNIT_ID
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}

/**
 * Helper to load and show Interstitial Ads.
 */
object InterstitialAdHelper {
    private var mInterstitialAd: InterstitialAd? = null

    fun loadAd(context: Context, adUnitId: String = AdConstants.SAMPLE_INTERSTITIAL_AD_UNIT_ID) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("AdMob", "Interstitial ad failed to load: ${adError.message}")
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("AdMob", "Interstitial ad loaded successfully")
                    mInterstitialAd = interstitialAd
                }
            }
        )
    }

    fun showAd(activity: Activity) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(activity)
            mInterstitialAd = null
            // Preload next ad
            loadAd(activity)
        } else {
            Log.d("AdMob", "The interstitial ad wasn't ready yet.")
            loadAd(activity)
        }
    }
}

/**
 * Helper to load and show Rewarded Interstitial Ads using your AdMob Unit ID.
 */
object RewardedAdHelper {
    private var rewardedAd: RewardedAd? = null
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null

    fun loadAd(
        context: Context,
        rewardedAdUnitId: String = AdConstants.REWARDED_AD_UNIT_ID,
        rewardedInterstitialAdUnitId: String = AdConstants.REWARDED_INTERSTITIAL_AD_UNIT_ID
    ) {
        val adRequest = AdRequest.Builder().build()

        // Load standard Rewarded Ad
        RewardedAd.load(
            context,
            rewardedAdUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d("AdMob", "Rewarded ad loaded successfully")
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d("AdMob", "Rewarded ad failed to load: ${error.message}")
                    rewardedAd = null
                }
            }
        )

        // Load Rewarded Interstitial Ad
        RewardedInterstitialAd.load(
            context,
            rewardedInterstitialAdUnitId,
            adRequest,
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    Log.d("AdMob", "Rewarded Interstitial ad loaded successfully")
                    rewardedInterstitialAd = ad
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.d("AdMob", "Rewarded Interstitial ad failed to load: ${loadAdError.message}")
                    rewardedInterstitialAd = null
                }
            }
        )
    }

    fun showAd(activity: Activity, onRewardEarned: (() -> Unit)? = null) {
        if (rewardedAd != null) {
            rewardedAd?.show(activity, OnUserEarnedRewardListener { rewardItem ->
                Log.d("AdMob", "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                onRewardEarned?.invoke()
            })
            rewardedAd = null
            loadAd(activity)
        } else if (rewardedInterstitialAd != null) {
            rewardedInterstitialAd?.show(activity, OnUserEarnedRewardListener { rewardItem ->
                Log.d("AdMob", "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                onRewardEarned?.invoke()
            })
            rewardedInterstitialAd = null
            loadAd(activity)
        } else {
            Log.d("AdMob", "Neither rewarded ad nor rewarded interstitial ad is ready yet. Reloading...")
            onRewardEarned?.invoke()
            loadAd(activity)
        }
    }
}
