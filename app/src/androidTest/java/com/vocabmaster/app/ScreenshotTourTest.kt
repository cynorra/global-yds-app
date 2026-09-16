package com.vocabmaster.app

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * CI-only design review aid: walks the real, signed-in app and dumps a screenshot per
 * screen to external files so a workflow can upload them as an artifact. Not a correctness
 * test - it never asserts. Login/password come from the instrumentation `-e` arguments
 * TEST_EMAIL / TEST_PASSWORD so no credentials are hardcoded here.
 */
@RunWith(AndroidJUnit4::class)
class ScreenshotTourTest {

    // Resource ids live under the R-class namespace (module `namespace`), which differs
    // from the installed applicationId below.
    private val resPkg = "com.vocabmaster.app"
    private val appPkg = "com.cynorra.vocabmaster.app"

    private val device: UiDevice =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    // Internal storage (filesDir) rather than getExternalFilesDir(null): the latter can
    // return null on a freshly booted CI emulator if external storage isn't mounted yet,
    // and File(null, child) silently resolves to a relative/unwritable path instead of
    // throwing - takeScreenshot() then fails too, but it returns a Boolean rather than
    // throwing, so the whole tour used to pass with zero screenshots ever written.
    private val outDir: File by lazy {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        File(ctx.filesDir, "screens").apply { mkdirs() }
    }

    private fun shot(name: String) {
        device.waitForIdle()
        Thread.sleep(700)
        outDir.mkdirs()
        val ok = device.takeScreenshot(File(outDir, name))
        android.util.Log.i("ScreenshotTour", "shot($name) outDir=${outDir.absolutePath} ok=$ok")
    }

    private fun launch(activity: Class<*>) {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(ctx, activity).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        ctx.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(appPkg).depth(0)), 15000)
    }

    @Test
    fun tour() {
        val args = InstrumentationRegistry.getArguments()
        val email = args.getString("TEST_EMAIL").orEmpty()
        val password = args.getString("TEST_PASSWORD").orEmpty()

        // 1) Onboarding carousel (AppIntro) - fresh install state.
        launch(OnboardingActivity::class.java)
        shot("01_onboarding_slide1.png")
        device.swipe(device.displayWidth * 4 / 5, device.displayHeight / 2, device.displayWidth / 5, device.displayHeight / 2, 15)
        device.waitForIdle()
        Thread.sleep(500)
        shot("02_onboarding_slide2.png")
        device.swipe(device.displayWidth * 4 / 5, device.displayHeight / 2, device.displayWidth / 5, device.displayHeight / 2, 15)
        device.waitForIdle()
        Thread.sleep(500)
        shot("03_onboarding_slide3.png")

        // 2) Login screen, direct intent (avoids relying on AppIntro's own Skip/Done button).
        launch(LoginActivity::class.java)
        device.wait(Until.hasObject(By.res(resPkg, "etEmail")), 8000)
        shot("04_login.png")

        // 3) Register screen, for comparison.
        device.findObject(By.res(resPkg, "tvGoRegister"))?.click()
        device.waitForIdle()
        Thread.sleep(600)
        shot("05_register.png")
        device.pressBack()
        device.waitForIdle()

        // 4) Sign in with the disposable test account and land on the home tab.
        if (email.isNotEmpty() && password.isNotEmpty()) {
            device.findObject(By.res(resPkg, "etEmail"))?.let {
                it.click()
                it.text = email
            }
            device.findObject(By.res(resPkg, "etPassword"))?.let {
                it.click()
                it.text = password
            }
            device.findObject(By.res(resPkg, "btnLogin"))?.click()
            device.wait(Until.hasObject(By.res(resPkg, "bottomNav")), 20000)
            Thread.sleep(1200)
            shot("06_home.png")

            val tabs = listOf(
                "nav_vocabulary" to "07_vocabulary.png",
                "nav_exam" to "08_exam.png",
                "nav_leaderboard" to "09_leaderboard.png",
                "nav_profile" to "10_profile.png",
            )
            for ((id, file) in tabs) {
                device.findObject(By.res(resPkg, id))?.click()
                device.waitForIdle()
                Thread.sleep(1200)
                shot(file)
            }
        }

        // 5) Premium paywall, direct intent - doesn't require being signed in.
        launch(PremiumActivity::class.java)
        Thread.sleep(800)
        shot("11_premium_paywall.png")
    }
}
