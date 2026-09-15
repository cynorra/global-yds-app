package com.vocabmaster.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.vocabmaster.app.data.billing.BillingManager

class PremiumActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_premium)

        findViewById<android.widget.ImageButton>(R.id.btnClose).setOnClickListener { finish() }

        bindFeatureRow(R.id.rowLeaderboard, R.string.premium_feature_leaderboard)
        bindFeatureRow(R.id.rowDailyExam, R.string.premium_feature_daily_exam)
        bindFeatureRow(R.id.rowMistakesBox, R.string.premium_feature_mistakes_box)
        bindFeatureRow(R.id.rowAdFree, R.string.premium_feature_ad_free)

        val btnUpgrade = findViewById<MaterialButton>(R.id.btnUpgrade)
        val btnRestore = findViewById<MaterialButton>(R.id.btnRestore)

        btnUpgrade.setOnClickListener {
            BillingManager.launchPurchaseFlow(this) {
                Snackbar.make(btnUpgrade, R.string.premium_purchase_unavailable, Snackbar.LENGTH_LONG).show()
            }
        }

        btnRestore.setOnClickListener {
            BillingManager.restorePurchases(this) { found ->
                runOnUiThread {
                    val messageRes = if (found) R.string.premium_already_owned else R.string.premium_restore_none_found
                    Snackbar.make(btnRestore, messageRes, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        BillingManager.isPremium.observe(this) { isPremium ->
            if (isPremium) {
                btnUpgrade.isEnabled = false
                btnUpgrade.text = getString(R.string.premium_already_owned)
            }
        }
    }

    private fun bindFeatureRow(rowId: Int, textRes: Int) {
        findViewById<android.view.View>(rowId).findViewById<TextView>(R.id.tvFeatureText).setText(textRes)
    }

    companion object {
        @JvmStatic
        fun createIntent(context: Context): Intent = Intent(context, PremiumActivity::class.java)
    }
}
