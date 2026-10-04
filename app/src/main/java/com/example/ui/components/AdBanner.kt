package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.billing.BillingManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
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
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3975025431204010/8766402527"
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
    val context = LocalContext.current
    val isAdsRemoved by BillingManager.getInstance(context).isAdsRemoved.collectAsStateWithLifecycle()

    if (isAdsRemoved) {
        // Ads removed via Subscription or Lifetime In-App Purchase
        return
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                AdView(ctx).apply {
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
    private var isLoading = false
    private var actionCount = 0

    // Frequency and session limits:
    // 1. Initial delay of 10 seconds after app launch/setup
    // 2. Minimum 45 seconds interval between consecutive ads
    // 3. Maximum 3 ads shown per app session
    private val appStartTimeMs: Long = SystemClock.elapsedRealtime()
    private const val INITIAL_DELAY_MS: Long = 10_000L // 10 seconds after app start
    private const val MIN_INTERVAL_BETWEEN_ADS_MS: Long = 45_000L // 45 seconds cooldown between ads
    private const val MAX_ADS_PER_SESSION: Int = 4 // Max 4 ads per app opening

    private var adsShownThisSession = 0
    private var lastAdShownTimeMs = 0L

    // Flag indicating whether user is currently inside a reading/suppressed Azkar or Dua screen
    @Volatile
    private var isUserReadingAdhkar: Boolean = false

    /**
     * Mark whether user is currently actively reciting or reading adhkar / dua.
     * While true, all regular interstitial ads are strictly suppressed so the user is never interrupted.
     */
    fun setUserReadingAdhkar(reading: Boolean) {
        isUserReadingAdhkar = reading
        Log.d("AdMob", "setUserReadingAdhkar set to: $reading")
    }

    fun isUserReadingAdhkar(): Boolean = isUserReadingAdhkar

    /**
     * Checks whether an interstitial ad is permitted to be shown according to frequency rules:
     * 1. Not currently reading Azkar or Dua
     * 2. Total ads this session < MAX_ADS_PER_SESSION
     * 3. At least 10 seconds have elapsed since app launch/setup
     * 4. At least 45 seconds have elapsed since the previous ad
     */
    fun canShowAd(ignoreReadingState: Boolean = false): Boolean {
        if (!ignoreReadingState && isUserReadingAdhkar) {
            Log.d("AdMob", "Ad blocked: User is currently reciting or reading Azkar/Dua.")
            return false
        }
        if (adsShownThisSession >= MAX_ADS_PER_SESSION) {
            Log.d("AdMob", "Ad blocked: Max ads per session ($MAX_ADS_PER_SESSION) reached ($adsShownThisSession shown).")
            return false
        }
        val now = SystemClock.elapsedRealtime()
        val timeSinceAppStart = now - appStartTimeMs
        if (timeSinceAppStart < INITIAL_DELAY_MS) {
            val remainingSec = (INITIAL_DELAY_MS - timeSinceAppStart) / 1000
            Log.d("AdMob", "Ad blocked: Initial delay active ($remainingSec seconds remaining).")
            return false
        }
        if (lastAdShownTimeMs > 0 && (now - lastAdShownTimeMs) < MIN_INTERVAL_BETWEEN_ADS_MS) {
            val remainingSec = (MIN_INTERVAL_BETWEEN_ADS_MS - (now - lastAdShownTimeMs)) / 1000
            Log.d("AdMob", "Ad blocked: Cooldown active ($remainingSec seconds remaining).")
            return false
        }
        return true
    }

    fun loadAd(
        context: Context,
        adUnitId: String = AdConstants.INTERSTITIAL_AD_UNIT_ID,
        fallbackToSample: Boolean = true
    ) {
        if (BillingManager.isAdsRemovedQuick(context) || adsShownThisSession >= MAX_ADS_PER_SESSION) {
            mInterstitialAd = null
            return
        }
        if (mInterstitialAd != null || isLoading) return
        isLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("AdMob", "Interstitial ad ($adUnitId) failed to load: ${adError.message} (code: ${adError.code})")
                    mInterstitialAd = null
                    isLoading = false
                    // If live ad unit failed to load (e.g. newly created unit propagation or no fill), fallback to sample ad unit so testing works
                    if (fallbackToSample && adUnitId != AdConstants.SAMPLE_INTERSTITIAL_AD_UNIT_ID) {
                        Log.d("AdMob", "Falling back to Sample Interstitial Unit for testing")
                        loadAd(context, AdConstants.SAMPLE_INTERSTITIAL_AD_UNIT_ID, fallbackToSample = false)
                    }
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("AdMob", "Interstitial ad ($adUnitId) loaded successfully")
                    mInterstitialAd = interstitialAd
                    isLoading = false
                }
            }
        )
    }

    fun showAd(activity: Activity, forceShow: Boolean = false, onAdClosed: (() -> Unit)? = null) {
        if (BillingManager.isAdsRemovedQuick(activity)) {
            onAdClosed?.invoke()
            return
        }

        if (!forceShow && !canShowAd()) {
            onAdClosed?.invoke()
            return
        }

        val ad = mInterstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("AdMob", "Interstitial ad dismissed")
                    mInterstitialAd = null
                    // Preload next ad only if session limit not yet reached
                    if (adsShownThisSession < MAX_ADS_PER_SESSION) {
                        loadAd(activity)
                    }
                    onAdClosed?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d("AdMob", "Interstitial ad failed to show: ${adError.message}")
                    mInterstitialAd = null
                    if (adsShownThisSession < MAX_ADS_PER_SESSION) {
                        loadAd(activity)
                    }
                    onAdClosed?.invoke()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("AdMob", "Interstitial ad showed")
                    mInterstitialAd = null
                    adsShownThisSession++
                    lastAdShownTimeMs = SystemClock.elapsedRealtime()
                    Log.d("AdMob", "Total ads shown this session: $adsShownThisSession / $MAX_ADS_PER_SESSION")
                }
            }
            ad.show(activity)
        } else {
            Log.d("AdMob", "The interstitial ad wasn't ready yet.")
            if (adsShownThisSession < MAX_ADS_PER_SESSION) {
                loadAd(activity)
            }
            onAdClosed?.invoke()
        }
    }

    fun showAd(activity: Activity, onAdClosed: (() -> Unit)?) {
        showAd(activity, forceShow = false, onAdClosed = onAdClosed)
    }

    /**
     * Shows an interstitial ad when user is exiting the app or finishing an auto azkar session.
     * Guaranteed to attempt display and then proceed with action.
     */
    fun showAdOnAppExit(activity: Activity, onFinished: () -> Unit) {
        if (BillingManager.isAdsRemovedQuick(activity)) {
            onFinished()
            return
        }
        val ad = mInterstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    mInterstitialAd = null
                    onFinished()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    mInterstitialAd = null
                    onFinished()
                }

                override fun onAdShowedFullScreenContent() {
                    mInterstitialAd = null
                    adsShownThisSession++
                    lastAdShownTimeMs = SystemClock.elapsedRealtime()
                }
            }
            ad.show(activity)
        } else {
            // Ad not preloaded, proceed to close smoothly
            onFinished()
        }
    }

    /**
     * Shows an interstitial ad when user completes an Auto Azkar session and presses complete.
     * User explicitly requested: "Amma idan an kammala auto azkar anyi pressing complete to definetly ads ta bude."
     * Guaranteed to open an interstitial ad (unless ads are removed via VIP subscription).
     * If already preloaded, shows immediately.
     * If not yet cached, loads on-demand with high priority and safety timeout before finishing.
     */
    fun showAdOnAutoAzkarComplete(activity: Activity, onFinished: () -> Unit) {
        // End reading state
        setUserReadingAdhkar(false)

        if (BillingManager.isAdsRemovedQuick(activity)) {
            onFinished()
            return
        }

        var hasFinished = false
        val finishOnce = {
            if (!hasFinished) {
                hasFinished = true
                onFinished()
            }
        }

        val ad = mInterstitialAd
        if (ad != null) {
            Log.d("AdMob", "Auto Azkar completed: Preloaded interstitial ad available, showing.")
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("AdMob", "Auto Azkar completed ad dismissed.")
                    mInterstitialAd = null
                    loadAd(activity)
                    finishOnce()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e("AdMob", "Auto Azkar completed ad failed to show: ${adError.message}")
                    mInterstitialAd = null
                    loadAd(activity)
                    finishOnce()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("AdMob", "Auto Azkar completed ad showed.")
                    mInterstitialAd = null
                    adsShownThisSession++
                    lastAdShownTimeMs = SystemClock.elapsedRealtime()
                }
            }
            ad.show(activity)
        } else {
            Log.d("AdMob", "Auto Azkar completed: Interstitial ad not cached yet, loading on-demand...")
            val handler = Handler(Looper.getMainLooper())
            val timeoutRunnable = Runnable {
                Log.d("AdMob", "Auto Azkar ad load timed out (safety for offline), finishing.")
                finishOnce()
            }
            handler.postDelayed(timeoutRunnable, 3500L)

            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                activity,
                AdConstants.INTERSTITIAL_AD_UNIT_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(loadedAd: InterstitialAd) {
                        handler.removeCallbacks(timeoutRunnable)
                        if (hasFinished || activity.isFinishing || activity.isDestroyed) return
                        Log.d("AdMob", "Auto Azkar on-demand live ad loaded successfully, showing.")
                        loadedAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                mInterstitialAd = null
                                finishOnce()
                            }

                            override fun onAdFailedToShowFullScreenContent(err: AdError) {
                                mInterstitialAd = null
                                finishOnce()
                            }

                            override fun onAdShowedFullScreenContent() {
                                mInterstitialAd = null
                                adsShownThisSession++
                                lastAdShownTimeMs = SystemClock.elapsedRealtime()
                            }
                        }
                        loadedAd.show(activity)
                    }

                    override fun onAdFailedToLoad(loadError: LoadAdError) {
                        Log.d("AdMob", "Auto Azkar live ad failed: ${loadError.message}, falling back to sample unit")
                        InterstitialAd.load(
                            activity,
                            AdConstants.SAMPLE_INTERSTITIAL_AD_UNIT_ID,
                            adRequest,
                            object : InterstitialAdLoadCallback() {
                                override fun onAdLoaded(sampleAd: InterstitialAd) {
                                    handler.removeCallbacks(timeoutRunnable)
                                    if (hasFinished || activity.isFinishing || activity.isDestroyed) return
                                    Log.d("AdMob", "Auto Azkar sample unit loaded, showing.")
                                    sampleAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                                        override fun onAdDismissedFullScreenContent() {
                                            mInterstitialAd = null
                                            finishOnce()
                                        }

                                        override fun onAdFailedToShowFullScreenContent(err: AdError) {
                                            mInterstitialAd = null
                                            finishOnce()
                                        }

                                        override fun onAdShowedFullScreenContent() {
                                            mInterstitialAd = null
                                            adsShownThisSession++
                                            lastAdShownTimeMs = SystemClock.elapsedRealtime()
                                        }
                                    }
                                    sampleAd.show(activity)
                                }

                                override fun onAdFailedToLoad(err: LoadAdError) {
                                    Log.e("AdMob", "Both live and sample ads failed to load: ${err.message}")
                                    handler.removeCallbacks(timeoutRunnable)
                                    finishOnce()
                                }
                            }
                        )
                    }
                }
            )
        }
    }

    /**
     * Schedules the first interstitial ad to appear 10 seconds after app start / setup.
     */
    fun scheduleAppLaunchAd(
        activity: Activity,
        adUnitId: String = AdConstants.INTERSTITIAL_AD_UNIT_ID
    ) {
        if (BillingManager.isAdsRemovedQuick(activity)) return
        // Pre-load the ad in advance so it is cached and ready when 10 seconds expire
        loadAd(activity, adUnitId)

        val timeAlreadyElapsed = SystemClock.elapsedRealtime() - appStartTimeMs
        val delay = (INITIAL_DELAY_MS - timeAlreadyElapsed).coerceAtLeast(1000L)

        Handler(Looper.getMainLooper()).postDelayed({
            try {
                if (!activity.isFinishing && !activity.isDestroyed && canShowAd()) {
                    Log.d("AdMob", "10 seconds initial delay elapsed; presenting 1st interstitial ad.")
                    showAd(activity)
                }
            } catch (e: Exception) {
                Log.e("AdMob", "Error showing delayed launch ad: ${e.message}")
            }
        }, delay)
    }

    @Deprecated("Use scheduleAppLaunchAd to respect 1-minute setup and quiet period")
    fun loadAndShowOnAppLaunch(
        activity: Activity,
        adUnitId: String = AdConstants.INTERSTITIAL_AD_UNIT_ID,
        fallbackToSample: Boolean = true
    ) {
        scheduleAppLaunchAd(activity, adUnitId)
    }

    fun triggerAdOnAction(activity: Activity, threshold: Int = 3, onAdClosed: (() -> Unit)? = null) {
        if (BillingManager.isAdsRemovedQuick(activity) || !canShowAd()) {
            onAdClosed?.invoke()
            return
        }
        actionCount++
        if (actionCount >= threshold) {
            actionCount = 0
            showAd(activity, onAdClosed)
        } else {
            onAdClosed?.invoke()
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
        if (BillingManager.isAdsRemovedQuick(context)) {
            rewardedAd = null
            rewardedInterstitialAd = null
            return
        }
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
        if (BillingManager.isAdsRemovedQuick(activity)) {
            onRewardEarned?.invoke()
            return
        }
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
