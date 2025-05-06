package com.example.newgemini;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 3000; // 3 seconds
    private ImageView logoImageView;
    private ImageView iconOne, iconTwo, iconThree;
    private TextView appNameText;
    private View progressLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // Initialize views
        logoImageView = findViewById(R.id.logoImageView);
        iconOne = findViewById(R.id.iconOne);
        iconTwo = findViewById(R.id.iconTwo);
        iconThree = findViewById(R.id.iconThree);
        appNameText = findViewById(R.id.appNameText);
        progressLine = findViewById(R.id.progressLine);

        // Set initial states
        logoImageView.setAlpha(0f);
        iconOne.setAlpha(0f);
        iconTwo.setAlpha(0f);
        iconThree.setAlpha(0f);
        appNameText.setAlpha(0f);
        progressLine.setScaleX(0f);

        // Start animations
        startAnimations();

        // Navigate to login after delay
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }, SPLASH_DELAY);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void startAnimations() {
        // Logo animation
        ObjectAnimator logoFadeIn = ObjectAnimator.ofFloat(logoImageView, "alpha", 0f, 1f);
        ObjectAnimator logoScale = ObjectAnimator.ofFloat(logoImageView, "scaleX", 0.3f, 1f);
        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(logoImageView, "scaleY", 0.3f, 1f);

        // Icons animations
        ObjectAnimator iconOneFade = ObjectAnimator.ofFloat(iconOne, "alpha", 0f, 1f);
        ObjectAnimator iconTwoFade = ObjectAnimator.ofFloat(iconTwo, "alpha", 0f, 1f);
        ObjectAnimator iconThreeFade = ObjectAnimator.ofFloat(iconThree, "alpha", 0f, 1f);

        // Text animation
        ObjectAnimator textFade = ObjectAnimator.ofFloat(appNameText, "alpha", 0f, 1f);

        // Progress line animation
        ObjectAnimator progressAnim = ObjectAnimator.ofFloat(progressLine, "scaleX", 0f, 1f);

        // Set up animation set
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AnticipateOvershootInterpolator());
        animatorSet.playTogether(logoFadeIn, logoScale, logoScaleY);
        animatorSet.setDuration(1000);

        // Secondary animations
        AnimatorSet secondarySet = new AnimatorSet();
        secondarySet.playSequentially(
                iconOneFade,
                iconTwoFade,
                iconThreeFade,
                textFade
        );
        secondarySet.setStartDelay(500);
        secondarySet.setDuration(300);

        // Progress animation
        ObjectAnimator progressAnimation = ObjectAnimator.ofFloat(progressLine, "scaleX", 0f, 1f);
        progressAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimation.setDuration(2000);
        progressAnimation.setStartDelay(1500);

        // Start all animations
        animatorSet.start();
        secondarySet.start();
        progressAnimation.start();
    }
}