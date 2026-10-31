package com.vocabmaster.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.vocabmaster.app.data.local.AppPreferences;

public class OnboardingActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Slide 1: Welcome
        addSlide(AppIntroFragment.createInstance(
                getString(R.string.onboarding_1_title),
                getString(R.string.onboarding_1_desc),
                R.drawable.ic_onboarding_success,
                R.color.colorPrimary
        ));

        // Slide 2: Vocabulary
        addSlide(AppIntroFragment.createInstance(
                getString(R.string.onboarding_2_title),
                getString(R.string.onboarding_2_desc),
                R.drawable.ic_onboarding_cards,
                R.color.colorPrimaryDark
        ));

        // Slide 3: Exams
        addSlide(AppIntroFragment.createInstance(
                getString(R.string.onboarding_3_title),
                getString(R.string.onboarding_3_desc),
                R.drawable.ic_onboarding_exam,
                R.color.colorAccent
        ));

        // Customize AppIntro
        setIndicatorColor(Color.WHITE, Color.GRAY);
        setSkipText(getString(R.string.onboarding_skip));
        setDoneText(getString(R.string.onboarding_done));
    }

    @Override
    protected void onSkipPressed(@Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        AppPreferences.setOnboardingComplete(this);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        AppPreferences.setOnboardingComplete(this);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
