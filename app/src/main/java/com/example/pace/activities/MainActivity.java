package com.example.pace.activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.pace.R;
import com.example.pace.fragments.*;

public class MainActivity extends AppCompatActivity {

    private ImageView iconHome, iconHistory, iconProgress, iconProfile;
    private TextView labelHome, labelHistory, labelProgress, labelProfile;
    private View badgeHome, badgeHistory, badgeProgress, badgeProfile;

    private View currentActiveBadge;
    private int mutedColor, limeColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mutedColor = ContextCompat.getColor(this, R.color.muted_fg);
        limeColor  = ContextCompat.getColor(this, R.color.lime);

        iconHome     = findViewById(R.id.iconHome);
        iconHistory  = findViewById(R.id.iconHistory);
        iconProgress = findViewById(R.id.iconProgress);
        iconProfile  = findViewById(R.id.iconProfile);
        labelHome    = findViewById(R.id.labelHome);
        labelHistory = findViewById(R.id.labelHistory);
        labelProgress= findViewById(R.id.labelProgress);
        labelProfile = findViewById(R.id.labelProfile);

        badgeHome     = findViewById(R.id.badgeHome);
        badgeHistory  = findViewById(R.id.badgeHistory);
        badgeProgress = findViewById(R.id.badgeProgress);
        badgeProfile  = findViewById(R.id.badgeProfile);

        // Default tab
        loadFragment(new HomeFragment());
        setActive(badgeHome, iconHome, labelHome);
    }

    public void switchToHistory() {
        loadFragment(new HistoryFragment());
        setActive(badgeHistory, iconHistory, labelHistory);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LinearLayout navHome     = findViewById(R.id.navHome);
        LinearLayout navHistory  = findViewById(R.id.navHistory);
        LinearLayout navProgress = findViewById(R.id.navProgress);
        LinearLayout navProfile  = findViewById(R.id.navProfile);
        View         navRun      = findViewById(R.id.navRun);

        // Catatan: highlight badge sudah punya animasi masuknya sendiri (animateBadgeIn),
        // jadi tidak perlu bounce() lagi di sini supaya animasinya tidak tabrakan/patah-patah.
        navHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            setActive(badgeHome, iconHome, labelHome);
        });
        navHistory.setOnClickListener(v -> {
            loadFragment(new HistoryFragment());
            setActive(badgeHistory, iconHistory, labelHistory);
        });
        navProgress.setOnClickListener(v -> {
            loadFragment(new ProgressFragment());
            setActive(badgeProgress, iconProgress, labelProgress);
        });
        navProfile.setOnClickListener(v -> {
            loadFragment(new ProfileFragment());
            setActive(badgeProfile, iconProfile, labelProfile);
        });
        navRun.setOnClickListener(v -> {
            loadFragment(new RunFragment());
            clearActive();
            bounce(navRun);
        });
    }

    private void loadFragment(Fragment f) {
        // Transisi halus (bukan fade polos): slide + scale + alpha
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_fade_in, R.anim.slide_fade_out)
                .replace(R.id.fragmentContainer, f)
                .commit();
    }

    private void clearActive() {
        animateBadgeOut(currentActiveBadge);
        animateIconColor(iconHome, mutedColor);
        animateIconColor(iconHistory, mutedColor);
        animateIconColor(iconProgress, mutedColor);
        animateIconColor(iconProfile, mutedColor);
        animateLabelColor(labelHome, mutedColor);
        animateLabelColor(labelHistory, mutedColor);
        animateLabelColor(labelProgress, mutedColor);
        animateLabelColor(labelProfile, mutedColor);
        currentActiveBadge = null;
    }

    private void setActive(View badge, ImageView icon, TextView label) {
        clearActive();
        badge.setSelected(true);
        currentActiveBadge = badge;
        animateBadgeIn(badge);
        animateIconColor(icon, limeColor);
        animateLabelColor(label, limeColor);
    }

    // Highlight pill muncul dengan animasi scale+alpha yang smooth, bukan potong langsung
    private void animateBadgeIn(View badge) {
        badge.setSelected(true);
        badge.setScaleX(0.6f);
        badge.setScaleY(0.6f);
        badge.setAlpha(0f);
        badge.animate()
                .scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(220)
                .setInterpolator(new OvershootInterpolator(1.6f))
                .start();
    }

    private void animateBadgeOut(View badge) {
        if (badge == null) return;
        badge.setSelected(false);
    }

    // Transisi warna ikon halus, bukan lompatan warna instan
    private void animateIconColor(ImageView icon, int toColor) {
        int from = icon.getColorFilter() != null ? currentColorOf(icon) : mutedColor;
        ValueAnimator anim = ValueAnimator.ofObject(new ArgbEvaluator(), from, toColor);
        anim.setDuration(180);
        anim.addUpdateListener(a -> icon.setColorFilter((int) a.getAnimatedValue()));
        anim.start();
    }

    private void animateLabelColor(TextView label, int toColor) {
        int from = label.getCurrentTextColor();
        ValueAnimator anim = ValueAnimator.ofObject(new ArgbEvaluator(), from, toColor);
        anim.setDuration(180);
        anim.addUpdateListener(a -> label.setTextColor((int) a.getAnimatedValue()));
        anim.start();
    }

    private int currentColorOf(ImageView icon) {
        return mutedColor;
    }

    // Animasi bounce saat tap
    private void bounce(View v) {
        v.animate().scaleX(0.85f).scaleY(0.85f).setDuration(80)
                .withEndAction(() ->
                        v.animate().scaleX(1f).scaleY(1f)
                                .setDuration(200)
                                .setInterpolator(new OvershootInterpolator(2f))
                                .start()
                ).start();
    }
}
