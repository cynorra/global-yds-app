package com.vocabmaster.app.util

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory

object InAppReviewManager {

    /**
     * Launches the in-app review flow if eligible.
     * Google automatically manages quotas and rate-limiting. If quota is exceeded or
     * offline, it completes gracefully without bothering the user.
     */
    fun requestReviewIfEligible(activity: Activity) {
        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful && !activity.isFinishing && !activity.isDestroyed) {
                val reviewInfo = task.result
                manager.launchReviewFlow(activity, reviewInfo)
            }
        }
    }
}

