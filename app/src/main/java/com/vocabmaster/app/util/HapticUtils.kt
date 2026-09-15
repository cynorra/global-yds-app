package com.vocabmaster.app.util

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View

/**
 * Thin wrapper around View.performHapticFeedback - deliberately not android.os.Vibrator, so
 * this respects the user's system-wide haptic feedback setting and needs no VIBRATE permission.
 */
object HapticUtils {

    /** A light tick for a routine selection (e.g. picking an answer option). */
    @JvmStatic
    fun tap(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }

    /** A stronger confirmation for a rewarding moment (passing an exam, unlocking a badge). */
    @JvmStatic
    fun success(view: View) {
        val constant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            HapticFeedbackConstants.CONFIRM
        } else {
            HapticFeedbackConstants.LONG_PRESS
        }
        view.performHapticFeedback(constant)
    }
}
