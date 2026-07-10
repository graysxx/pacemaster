package com.example.pace.fragments;

import android.os.Bundle;
import android.view.*;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import androidx.fragment.app.Fragment;
import com.example.pace.R;

public class ProgressFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);
        setupProgressBar(view);
        return view;
    }

    private void setupProgressBar(View view) {
        View progressBar = view.findViewById(R.id.progressMonthly);
        progressBar.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        progressBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int parentWidth = ((FrameLayout) progressBar.getParent()).getWidth();
                        ViewGroup.LayoutParams lp = progressBar.getLayoutParams();
                        lp.width = (int)(parentWidth * 0.72f); // 72% = 108/150 km
                        progressBar.setLayoutParams(lp);

                        // Animasi dari kiri
                        progressBar.setScaleX(0f);
                        progressBar.setPivotX(0f);
                        progressBar.animate()
                                .scaleX(1f)
                                .setDuration(900)
                                .setStartDelay(200)
                                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                                .start();
                    }
                });
    }
}