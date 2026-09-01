package com.example.billing

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Product & Subscription IDs configured for Google Play Console.
 */
object BillingConstants {
    const val PREFS_NAME = "noor_zikir_billing_prefs"
    const val KEY_IS_ADS_REMOVED = "is_ads_removed"
    const val KEY_PURCHASE_TIME = "purchase_time"
    const val KEY_ACTIVE_PRODUCT_ID = "active_product_id"
    const val KEY_PURCHASE_TOKEN = "purchase_token"

    // Subscriptions: Weekly, Monthly, Yearly
    const val SUB_WEEKLY = "noor_zikir_remove_ads_weekly"
    const val SUB_MONTHLY = "noor_zikir_remove_ads_monthly"
    const val SUB_YEARLY = "noor_zikir_remove_ads_yearly"

    // Alternative IDs
    const val SUB_WEEKLY_ALT = "remove_ads_weekly"
    const val SUB_MONTHLY_ALT = "remove_ads_monthly"
    const val SUB_YEARLY_ALT = "remove_ads_yearly"

    val SUBSCRIPTION_IDS = listOf(
        SUB_WEEKLY,
        SUB_MONTHLY,
        SUB_YEARLY,
        SUB_WEEKLY_ALT,
        SUB_MONTHLY_ALT,
        SUB_YEARLY_ALT
    )
}

/**
 * UI-friendly representation of a subscription tier.
 */
data class PremiumPlan(
    val productId: String,
    val isSubscription: Boolean,
    val title: String,
    val subtitle: String,
    val formattedPrice: String,
    val badge: String? = null,
    val productDetails: ProductDetails,
    val offerToken: String? = null
)

/**
 * Singleton Google Play Billing Manager handling subscriptions (Weekly, Monthly, Yearly) and state sync.
 */
class BillingManager private constructor(context: Context) : PurchasesUpdatedListener, BillingClientStateListener {

    private val appContext = context.applicationContext
    private val sharedPrefs: SharedPreferences =
        appContext.getSharedPreferences(BillingConstants.PREFS_NAME, Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isAdsRemoved = MutableStateFlow(sharedPrefs.getBoolean(BillingConstants.KEY_IS_ADS_REMOVED, false))
    val isAdsRemoved: StateFlow<Boolean> = _isAdsRemoved.asStateFlow()

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting.asStateFlow()

    private val _availablePlans = MutableStateFlow<List<PremiumPlan>>(emptyList())
    val availablePlans: StateFlow<List<PremiumPlan>> = _availablePlans.asStateFlow()

    private val _billingStatusMessage = MutableStateFlow<String?>(null)
    val billingStatusMessage: StateFlow<String?> = _billingStatusMessage.asStateFlow()

    private var billingClient: BillingClient = createBillingClient()
    private var retryCount = 0
    private val maxRetries = 4

    init {
        startBillingConnection()
    }

    private fun createBillingClient(): BillingClient {
        val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .enablePrepaidPlans()
            .build()

        return BillingClient.newBuilder(appContext)
            .setListener(this)
            .enablePendingPurchases(pendingPurchasesParams)
            .build()
    }

    fun startBillingConnection() {
        if (billingClient.isReady) {
            queryExistingPurchases()
            queryAvailableProducts()
            return
        }

        _isConnecting.value = true
        try {
            billingClient.startConnection(this)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting billing connection: ${e.message}")
            _isConnecting.value = false
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        _isConnecting.value = false
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            Log.d(TAG, "Google Play Billing Setup finished successfully.")
            retryCount = 0
            queryExistingPurchases()
            queryAvailableProducts()
        } else {
            Log.w(TAG, "Billing setup failed with responseCode: ${billingResult.responseCode} - ${billingResult.debugMessage}")
            handleConnectionRetry()
        }
    }

    override fun onBillingServiceDisconnected() {
        Log.w(TAG, "Billing Service Disconnected.")
        _isConnecting.value = false
        handleConnectionRetry()
    }

    private fun handleConnectionRetry() {
        if (retryCount < maxRetries) {
            retryCount++
            val delayMs = 1000L * (1 shl retryCount)
            Log.d(TAG, "Retrying billing connection (attempt $retryCount) in ${delayMs}ms...")
            scope.launch {
                kotlinx.coroutines.delay(delayMs)
                startBillingConnection()
            }
        }
    }

    /**
     * Query weekly, monthly, and yearly subscriptions from Google Play.
     */
    fun queryAvailableProducts() {
        if (!billingClient.isReady) {
            startBillingConnection()
            return
        }

        scope.launch {
            val subProductsList = BillingConstants.SUBSCRIPTION_IDS.map { id ->
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(id)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            }

            val plans = mutableListOf<PremiumPlan>()

            try {
                val subParams = QueryProductDetailsParams.newBuilder()
                    .setProductList(subProductsList)
                    .build()

                val subResult = billingClient.queryProductDetails(subParams)
                if (subResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val productDetailsList = subResult.productDetailsList ?: emptyList()
                    for (details in productDetailsList) {
                        val subOffers = details.subscriptionOfferDetails
                        val baseOffer = subOffers?.firstOrNull()
                        val pricingPhase = baseOffer?.pricingPhases?.pricingPhaseList?.firstOrNull()
                        val formattedPrice = pricingPhase?.formattedPrice ?: "Subscription"

                        val isYearly = details.productId.contains("yearly", ignoreCase = true)
                        val isWeekly = details.productId.contains("weekly", ignoreCase = true)

                        val title = when {
                            isYearly -> "Yearly Subscription"
                            isWeekly -> "Weekly Subscription"
                            else -> "Monthly Subscription"
                        }

                        val subtitle = when {
                            isYearly -> "Save 45% • 1 Year ad-free access"
                            isWeekly -> "Billed weekly • Flexible short-term"
                            else -> "Billed monthly • Cancel anytime"
                        }

                        val badge = when {
                            isYearly -> "Best Value (Save 45%)"
                            isWeekly -> "Trial"
                            else -> "Popular"
                        }

                        plans.add(
                            PremiumPlan(
                                productId = details.productId,
                                isSubscription = true,
                                title = title,
                                subtitle = subtitle,
                                formattedPrice = formattedPrice,
                                badge = badge,
                                productDetails = details,
                                offerToken = baseOffer?.offerToken
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed querying subscription product details: ${e.message}")
            }

            // Sort plans in order: Weekly, Monthly, Yearly
            val sortedPlans = plans.sortedBy { plan ->
                when {
                    plan.productId.contains("weekly", ignoreCase = true) -> 1
                    plan.productId.contains("monthly", ignoreCase = true) -> 2
                    plan.productId.contains("yearly", ignoreCase = true) -> 3
                    else -> 4
                }
            }

            withContext(Dispatchers.Main) {
                _availablePlans.value = sortedPlans
                Log.d(TAG, "Loaded ${sortedPlans.size} available subscription plans from Play Store.")
            }
        }
    }

    /**
     * Query existing active subscriptions.
     */
    fun queryExistingPurchases() {
        if (!billingClient.isReady) {
            return
        }

        scope.launch {
            var hasActivePurchase = false
            var activeProductId: String? = null
            var activeToken: String? = null

            // Query Active Subscriptions
            val subParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()

            val subResult = billingClient.queryPurchasesAsync(subParams)
            if (subResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in subResult.purchasesList) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        hasActivePurchase = true
                        activeProductId = purchase.products.firstOrNull()
                        activeToken = purchase.purchaseToken
                        handlePurchaseAcknowledgement(purchase)
                    }
                }
            }

            updateAdsRemovedStatus(hasActivePurchase, activeProductId, activeToken)
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (!purchases.isNullOrEmpty()) {
                    for (purchase in purchases) {
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            handlePurchaseAcknowledgement(purchase)
                        }
                    }
                    _billingStatusMessage.value = "Alhamdulillah! Subscription successful. Ads removed!"
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.d(TAG, "User canceled purchase flow.")
                _billingStatusMessage.value = "Purchase canceled."
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Log.d(TAG, "Item already owned by user.")
                queryExistingPurchases()
                _billingStatusMessage.value = "You already have an active subscription. Ads are removed!"
            }
            else -> {
                Log.e(TAG, "Purchase failed: code=${billingResult.responseCode}, ${billingResult.debugMessage}")
                _billingStatusMessage.value = "Purchase error: ${billingResult.debugMessage}"
            }
        }
    }

    private fun handlePurchaseAcknowledgement(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.acknowledgePurchase(acknowledgeParams) { result ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Subscription acknowledged successfully: ${purchase.orderId}")
                } else {
                    Log.e(TAG, "Failed acknowledging subscription: ${result.debugMessage}")
                }
            }
        }

        updateAdsRemovedStatus(
            isRemoved = true,
            productId = purchase.products.firstOrNull(),
            token = purchase.purchaseToken,
            time = purchase.purchaseTime
        )
    }

    private fun updateAdsRemovedStatus(
        isRemoved: Boolean,
        productId: String? = null,
        token: String? = null,
        time: Long = System.currentTimeMillis()
    ) {
        sharedPrefs.edit().apply {
            putBoolean(BillingConstants.KEY_IS_ADS_REMOVED, isRemoved)
            if (productId != null) putString(BillingConstants.KEY_ACTIVE_PRODUCT_ID, productId)
            if (token != null) putString(BillingConstants.KEY_PURCHASE_TOKEN, token)
            putLong(BillingConstants.KEY_PURCHASE_TIME, time)
            apply()
        }

        _isAdsRemoved.value = isRemoved
        Log.d(TAG, "Ads Removed Status Updated: isAdsRemoved=$isRemoved, product=$productId")
    }

    /**
     * Launch Google Play purchase flow for a selected subscription plan.
     */
    fun launchPurchaseFlow(activity: Activity, plan: PremiumPlan): BillingResult {
        if (!billingClient.isReady) {
            startBillingConnection()
            return BillingResult.newBuilder()
                .setResponseCode(BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE)
                .setDebugMessage("Connecting to Google Play. Please retry in a moment.")
                .build()
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder().apply {
                setProductDetails(plan.productDetails)
                if (plan.offerToken != null) {
                    setOfferToken(plan.offerToken)
                }
            }.build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        return billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    /**
     * Restore previous active subscriptions for users reinstalling or switching devices.
     */
    fun restorePurchases(onResult: (Boolean, String) -> Unit) {
        if (!billingClient.isReady) {
            startBillingConnection()
            onResult(false, "Connecting to Google Play Store... Please try again in 5 seconds.")
            return
        }

        scope.launch {
            var foundActive = false
            val subParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
            val subResult = billingClient.queryPurchasesAsync(subParams)

            for (purchase in subResult.purchasesList) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    foundActive = true
                    handlePurchaseAcknowledgement(purchase)
                }
            }

            withContext(Dispatchers.Main) {
                if (foundActive) {
                    onResult(true, "Alhamdulillah! Your premium subscription was restored successfully.")
                } else {
                    onResult(false, "No active subscriptions found for this Google account.")
                }
            }
        }
    }

    fun clearStatusMessage() {
        _billingStatusMessage.value = null
    }

    companion object {
        private const val TAG = "BillingManager"

        @Volatile
        private var INSTANCE: BillingManager? = null

        fun getInstance(context: Context): BillingManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BillingManager(context).also { INSTANCE = it }
            }
        }

        /**
         * Fast synchronous check from local preferences.
         */
        fun isAdsRemovedQuick(context: Context): Boolean {
            val prefs = context.getSharedPreferences(BillingConstants.PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(BillingConstants.KEY_IS_ADS_REMOVED, false)
        }
    }
}
