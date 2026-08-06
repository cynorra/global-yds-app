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
import com.vocabmaster.app.ui.ProfileFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_OPEN_TAB = "extra_open_tab";
    public static final String TAB_VOCABULARY = "vocabulary";

    private BottomNavigationView bottomNav;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNav = findViewById(R.id.bottomNav);

        MobileAds.initialize(this, initializationStatus -> {});
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

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
        super.onDestroy();
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
