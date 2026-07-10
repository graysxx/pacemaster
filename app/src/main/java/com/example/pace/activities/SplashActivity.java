package com.example.pace.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pace.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Animasi pulse tiap dot
        animateDot(findViewById(R.id.dot1), 0);
        animateDot(findViewById(R.id.dot2), 150);
        animateDot(findViewById(R.id.dot3), 300);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2400);
    }

    private void animateDot(View dot, long delay) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(dot, "alpha", 0.3f, 1f, 0.3f);
        anim.setDuration(900);
        anim.setStartDelay(delay);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }
}