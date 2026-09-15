package com.vocabmaster.app.data.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.vocabmaster.app.data.local.AppPreferences

/**
 * Wraps Google Play Billing for the single lifetime "Premium" unlock (Leaderboard, Daily Exam,
 * Mistakes Box, and an ad-free experience). One product, one entitlement - no subscriptions.
 *
 * The product ID below MUST be created as a one-time (managed) product in the Play Console
 * with this exact ID before purchases can succeed there; sandbox/internal testing works once
 * the app is uploaded to at least an internal testing track.
 */
object BillingManager {
    const val PREMIUM_PRODUCT_ID = "vocabmaster_premium_lifetime"

    private const val TAG = "BillingManager"

    private val _isPremium = MutableLiveData(false)

    @JvmStatic
    val isPremium: LiveData<Boolean> get() = _isPremium

    private var billingClient: BillingClient? = null

    @JvmStatic
    fun initialize(context: Context) {
        val appContext = context.applicationContext
        // Seed from the local cache immediately so gating UI doesn't flicker while billing
        // connects (Play Billing setup is a network round trip).
        _isPremium.postValue(AppPreferences.isPremiumCached(appContext))

        val client = BillingClient.newBuilder(appContext)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    purchases.forEach { handlePurchase(appContext, it) }
                }
            }
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .build()
        billingClient = client

        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryExistingPurchases(appContext)
                } else {
                    Log.w(TAG, "Billing setup failed: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected")
            }
        })
    }

    private fun queryExistingPurchases(context: Context, onResult: ((found: Boolean) -> Unit)? = null) {
        val client = billingClient
        if (client == null) {
            onResult?.invoke(false)
            return
        }
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        client.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.w(TAG, "queryPurchasesAsync failed: ${billingResult.debugMessage}")
                onResult?.invoke(false)
                return@queryPurchasesAsync
            }
            val owned = purchases.any {
                it.products.contains(PREMIUM_PRODUCT_ID) && it.purchaseState == Purchase.PurchaseState.PURCHASED
            }
            setPremium(context, owned)
            purchases.forEach { handlePurchase(context, it) }
            onResult?.invoke(owned)
        }
    }

    /** User-initiated re-check, e.g. a "Restore Purchases" button on the paywall. */
    fun restorePurchases(context: Context, onResult: (found: Boolean) -> Unit) {
        queryExistingPurchases(context.applicationContext, onResult)
    }

    private fun handlePurchase(context: Context, purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        if (!purchase.products.contains(PREMIUM_PRODUCT_ID)) return

        setPremium(context, true)

        if (!purchase.isAcknowledged) {
            val client = billingClient ?: return
            val ackParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            client.acknowledgePurchase(ackParams) { result ->
                if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                    Log.w(TAG, "acknowledgePurchase failed: ${result.debugMessage}")
                }
            }
        }
    }

    private fun setPremium(context: Context, premium: Boolean) {
        AppPreferences.setPremiumCached(context, premium)
        _isPremium.postValue(premium)

        // Mirror onto the user's Firestore profile too, so other server-side logic (or a future
        // web dashboard) can see entitlement without talking to Play Billing directly. Billing
        // itself (queried above) remains the source of truth on-device.
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        try {
            FirebaseFirestore.getInstance().collection("users").document(uid)
                .set(mapOf("isPremium" to premium), SetOptions.merge())
        } catch (e: Exception) {
            Log.w(TAG, "Failed to mirror premium status to Firestore", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    /**
     * Looks up the product's price/details and launches Google's purchase UI.
     * [onProductUnavailable] fires if billing isn't connected or the product isn't configured
     * yet in the Play Console (e.g. during development before the product is set up there).
     */
    fun launchPurchaseFlow(activity: Activity, onProductUnavailable: () -> Unit) {
        val client = billingClient
        if (client == null || !client.isReady) {
            onProductUnavailable()
            return
        }

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PREMIUM_PRODUCT_ID)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

        client.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            val details: ProductDetails? = productDetailsList.firstOrNull()
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK || details == null) {
                Log.w(TAG, "Product details unavailable: ${billingResult.debugMessage}")
                onProductUnavailable()
                return@queryProductDetailsAsync
            }

            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(details)
                    .build()
            )
            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
            client.launchBillingFlow(activity, flowParams)
        }
    }

    @JvmStatic
    fun endConnection() {
        billingClient?.endConnection()
        billingClient = null
    }
}
