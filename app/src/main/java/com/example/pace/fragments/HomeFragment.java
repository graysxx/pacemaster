package com.example.pace.fragments;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ViewTreeObserver;
import androidx.fragment.app.Fragment;
import com.example.pace.R;
import com.example.pace.activities.MainActivity;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setupStartButton(view);
        setupProgressBar(view);
        setupWeather(view);

        view.findViewById(R.id.tvSeeAll).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToHistory();
            }
        });

        return view;
    }

    private void setupStartButton(View view) {
        Button btnStart = view.findViewById(R.id.btnStartRun);

        btnStart.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80)
                    .withEndAction(() -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(150)
                                .setInterpolator(new android.view.animation.OvershootInterpolator(2f))
                                .start();
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                .replace(R.id.fragmentContainer, new RunFragment())
                                .commit();
                    }).start();
        });

        // Glow effect
        FrameLayout glowWrap = (FrameLayout) btnStart.getParent();
        glowWrap.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        glowWrap.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        applyGlow(glowWrap);
                    }
                });
    }

    private void applyGlow(View glowWrap) {
        // radial gradient glow di belakang tombol
        GradientDrawable glow = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        0x00C8F43A,  // transparan
                        0x30C8F43A,  // lime 19% opacity
                        0x55C8F43A,  // lime 33% opacity tengah
                        0x30C8F43A,
                        0x00C8F43A
                }
        );
        glow.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        glow.setGradientRadius(glowWrap.getWidth() * 0.6f);
        glow.setCornerRadius(80f);
        glowWrap.setBackground(glow);
    }

    private void setupProgressBar(View view) {
        // Set lebar progress bar sesuai persentase (90%)
        View progressBar = view.findViewById(R.id.progressBar);
        progressBar.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        progressBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int parentWidth = ((View) progressBar.getParent()).getWidth();
                        ViewGroup.LayoutParams lp = progressBar.getLayoutParams();
                        lp.width = (int)(parentWidth * 0.90f); // 90% = 36.2/40
                        progressBar.setLayoutParams(lp);

                        // Animasi progress bar
                        progressBar.setScaleX(0f);
                        progressBar.setPivotX(0f);
                        progressBar.animate().scaleX(1f).setDuration(800)
                                .setStartDelay(300)
                                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                                .start();
                    }
                });
    }

    private void setupWeather(View view) {
        // Simulasi cuaca
        // Kondisi: 0=Cerah, 1=Berawan, 2=Hujan, 3=Gerimis, 4=Mendung
        int weatherCondition = getSimulatedWeather();

        ImageView ivWeather = view.findViewById(R.id.ivWeatherIcon);
        ImageView ivWeatherBg = view.findViewById(R.id.ivWeatherBg);
        TextView tvTemp     = view.findViewById(R.id.tvWeatherTemp);
        TextView tvLabel    = view.findViewById(R.id.tvWeatherLabel);
        View cardWeather = view.findViewById(R.id.cardWeather);

        ivWeatherBg.setVisibility(View.VISIBLE);

        switch (weatherCondition) {
            case 0: // Cerah
                ivWeather.setImageResource(R.drawable.ic_weather_sunny);
                ivWeatherBg.setImageResource(R.drawable.ic_weather_sunny); // Placeholder for actual bg image
                tvTemp.setText("28°");
                tvLabel.setText("Cerah");
                break;
            case 1: // Berawan
                ivWeather.setImageResource(R.drawable.ic_weather_cloudy);
                ivWeatherBg.setImageResource(R.drawable.ic_weather_cloudy);
                tvTemp.setText("25°");
                tvLabel.setText("Berawan");
                break;
            case 2: // Hujan
                ivWeather.setImageResource(R.drawable.ic_weather_rain);
                ivWeatherBg.setImageResource(R.drawable.ic_weather_rain);
                tvTemp.setText("22°");
                tvLabel.setText("Hujan");
                break;
            case 3: // Gerimis
                ivWeather.setImageResource(R.drawable.ic_weather_drizzle);
                ivWeatherBg.setImageResource(R.drawable.ic_weather_drizzle);
                tvTemp.setText("23°");
                tvLabel.setText("Gerimis");
                break;
            default: // Mendung
                ivWeather.setImageResource(R.drawable.ic_weather_cloudy);
                ivWeatherBg.setImageResource(R.drawable.ic_weather_cloudy);
                tvTemp.setText("24°");
                tvLabel.setText("Mendung");
        }

        cardWeather.setOnClickListener(v -> {
            // Toast or Dialog for Weather Detail
            android.widget.Toast.makeText(getContext(), "Detail Cuaca: " + tvLabel.getText(), android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    private int getSimulatedWeather() {
        // replace API call OpenWeatherMap
        // Simulasi berdasarkan jam (temp sim)
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        if (hour >= 6 && hour <= 11) return 0;  // Pagi = cerah
        if (hour >= 12 && hour <= 14) return 1; // Siang = berawan
        if (hour >= 15 && hour <= 17) return 3; // Sore = gerimis
        return 2;                               // Malam = hujan
    }
}