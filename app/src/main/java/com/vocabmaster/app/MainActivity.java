package com.vocabmaster.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.vocabmaster.app.ui.HomeFragment;
import com.vocabmaster.app.ui.VocabularyFragment;
import com.vocabmaster.app.ui.ExamFragment;
import com.vocabmaster.app.ui.LeaderboardFragment;
import com.vocabmaster.app.ui.ProfileFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import com.vocabmaster.app.data.billing.BillingManager;
import com.vocabmaster.app.data.remote.RemoteConfigManager;

import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_OPEN_TAB = "extra_open_tab";
    public static final String TAB_VOCABULARY = "vocabulary";

    // GDPR/UK consent (Google User Messaging Platform) flow. When true, defers ad init until
    // Google's UMP SDK has resolved consent (shows an EEA/UK consent form only where legally
    // required) before loading any ads.
    private static final boolean GDPR_CONSENT_ENABLED = true;

    private BottomNavigationView bottomNav;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNav = findViewById(R.id.bottomNav);

        adView = findViewById(R.id.adView);

        BillingManager.initialize(this);
        RemoteConfigManager.INSTANCE.initialize();
        BillingManager.isPremium().observe(this, isPremium -> {
            adView.setVisibility(isPremium ? View.GONE : View.VISIBLE);
        });
        initializeAds();

        // Load Home Fragment by default, unless we were opened via a deep link (e.g. a reminder notification)
        if (savedInstanceState == null) {
            if (TAB_VOCABULARY.equals(getIntent().getStringExtra(EXTRA_OPEN_TAB))) {
                loadFragment(new VocabularyFragment());
                bottomNav.setSelectedItemId(R.id.nav_vocabulary);
            } else {
                loadFragment(new HomeFragment());
                bottomNav.setSelectedItemId(R.id.nav_home);
            }
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (id == R.id.nav_vocabulary) {
                loadFragment(new VocabularyFragment());
                return true;
            } else if (id == R.id.nav_exam) {
                loadFragment(new ExamFragment());
                return true;
            } else if (id == R.id.nav_leaderboard) {
                if (Boolean.TRUE.equals(BillingManager.isPremium().getValue())) {
                    loadFragment(new LeaderboardFragment());
                    return true;
                }
                startActivity(PremiumActivity.createIntent(this));
                return false;
            } else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (isFinishing()) {
            BillingManager.endConnection();
        }
        super.onDestroy();
    }

    /**
     * When GDPR_CONSENT_ENABLED is false, initializes ads and loads the banner immediately,
     * no consent form. When true (default), defers ad init until Google's UMP SDK has resolved
     * consent (shows an EEA/UK consent form only where legally required); falls back to
     * initializing ads anyway if the consent info request itself fails (e.g. offline), same as
     * the no-consent-flow behavior.
     */
    private void initializeAds() {
        if (!GDPR_CONSENT_ENABLED) {
            MobileAds.initialize(this, initializationStatus -> {});
            loadBannerAd();
            return;
        }

        ConsentInformation consentInformation = UserMessagingPlatform.getConsentInformation(this);
        ConsentRequestParameters params = new ConsentRequestParameters.Builder().build();

        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(this, formError -> {
                    if (consentInformation.canRequestAds()) {
                        MobileAds.initialize(this, initializationStatus -> {});
                        loadBannerAd();
                    }
                }),
                requestConsentError -> {
                    MobileAds.initialize(this, initializationStatus -> {});
                    loadBannerAd();
                }
        );
    }

    private void loadBannerAd() {
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Log.w("MainActivity", "Banner ad failed to load: " + adError.getMessage());
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void loadFragment(Fragment fragment) {
        openFragment(fragment, false);
    }

    /**
     * Opens a fragment on top of the current tab. Used by HomeFragment to drill into
     * Grammar/Reading, which are not bottom-nav tabs of their own.
     */
    public void openFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (addToBackStack) {
            // Drilling into a sub-screen (e.g. Home -> Grammar/Reading): forward push slides in
            // from the right; popping the back stack (system/toolbar back) reverses it, sliding
            // the previous screen back in from the left.
            ft.setCustomAnimations(
                    R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            // Sibling-level bottom-nav tab switch: a simple cross-fade (no back-stack entry).
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        }
        ft.replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    public void selectBottomNavTab(int itemId) {
        bottomNav.setSelectedItemId(itemId);
    }
}
