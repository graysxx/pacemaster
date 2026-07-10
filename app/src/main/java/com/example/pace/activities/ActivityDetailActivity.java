package com.example.pace.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pace.views.LineChartView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.example.pace.R;
import com.example.pace.models.ActivityRecord;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ActivityDetailActivity extends AppCompatActivity {

    private View btnRingkasan, btnSplit;
    private TextView tvRingkasan, tvSplit;
    private View layoutRingkasan, layoutSplit;
    private LineChartView chartPace, chartKadens, chartElevasi;
    private ActivityRecord activityData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get data from intent
        activityData = (ActivityRecord) getIntent().getSerializableExtra("ACTIVITY_DATA");

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnRingkasan = findViewById(R.id.btnTabRingkasan);
        btnSplit = findViewById(R.id.btnTabSplit);
        layoutRingkasan = findViewById(R.id.layoutRingkasan);
        layoutSplit = findViewById(R.id.layoutSplit);

        chartPace = findViewById(R.id.chartPace);
        chartKadens = findViewById(R.id.chartKadens);
        chartElevasi = findViewById(R.id.chartElevasi);

        if (activityData != null) {
            populateData();
        } else {
            // Fallback to mock if null (for safety during dev)
            setupCharts();
        }

        btnRingkasan.setOnClickListener(v -> showRingkasan());
        btnSplit.setOnClickListener(v -> showSplit());

        findViewById(R.id.btnShare).setOnClickListener(v -> shareActivity());

        showRingkasan();
    }

    private void populateData() {
        ((TextView) findViewById(R.id.tvTitle)).setText(activityData.getTitle());
        ((TextView) findViewById(R.id.tvDateTime)).setText(activityData.getDateTime() + " · 📍 " + activityData.getLocation());
        ((TextView) findViewById(R.id.tvDetailJarak)).setText(String.format("%.2f", activityData.getDistance()));
        ((TextView) findViewById(R.id.tvHeaderJarak)).setText(String.format("%.2f km", activityData.getDistance()));
        ((TextView) findViewById(R.id.tvDetailPace)).setText(activityData.getAvgPace());
        ((TextView) findViewById(R.id.tvDetailWaktu)).setText(activityData.getDuration());
        
        ((TextView) findViewById(R.id.tvCalories)).setText(String.valueOf(activityData.getCalories()));
        ((TextView) findViewById(R.id.tvSteps)).setText(String.format("%,d", activityData.getSteps()));
        ((TextView) findViewById(R.id.tvDetailElev)).setText(activityData.getElevationGain() + " m");
        ((TextView) findViewById(R.id.tvMaxElev)).setText(activityData.getMaxElevation() + " m");

        // Charts
        if (activityData.getPaceData() != null) {
            chartPace.setData(activityData.getPaceData(), activityData.getXLabels(), new String[]{"6:00", "5:30", "5:00"}, ContextCompat.getColor(this, R.color.lime));
        }
        if (activityData.getCadenceData() != null) {
            chartKadens.setData(activityData.getCadenceData(), activityData.getXLabels(), new String[]{"180", "170", "160"}, ContextCompat.getColor(this, R.color.purple));
        }
        if (activityData.getElevationData() != null) {
            chartElevasi.setData(activityData.getElevationData(), activityData.getXLabels(), new String[]{"20", "10", "0"}, ContextCompat.getColor(this, R.color.orange));
        }

        // Splits Table
        populateSplits();
    }

    private void populateSplits() {
        LinearLayout splitContainer = findViewById(R.id.splitContainer);
        // Clear existing mock items (except header)
        int childCount = splitContainer.getChildCount();
        if (childCount > 1) {
            splitContainer.removeViews(1, childCount - 1);
        }

        if (activityData.getSplits() != null) {
            for (ActivityRecord.Split split : activityData.getSplits()) {
                View row = LayoutInflater.from(this).inflate(R.layout.item_split_row, splitContainer, false);
                ((TextView) row.findViewById(R.id.tvSplitKm)).setText(String.valueOf(split.km));
                ((TextView) row.findViewById(R.id.tvSplitPace)).setText(split.pace);
                ((TextView) row.findViewById(R.id.tvSplitElev)).setText(split.elev);
                ((android.widget.ProgressBar) row.findViewById(R.id.pbSplitBar)).setProgress(split.progress);
                
                if (split.progress >= 90) { // Highlight fastest split
                    ((TextView) row.findViewById(R.id.tvSplitKm)).setTextColor(ContextCompat.getColor(this, R.color.lime));
                    ((TextView) row.findViewById(R.id.tvSplitPace)).setTextColor(ContextCompat.getColor(this, R.color.lime));
                }
                
                splitContainer.addView(row);
            }
        }
    }

    private void showRingkasan() {
        btnRingkasan.setBackgroundResource(R.drawable.btn_lime);
        ((TextView)findViewById(R.id.tvTabRingkasan)).setTextColor(getResources().getColor(R.color.bg));
        
        btnSplit.setBackground(null);
        ((TextView)findViewById(R.id.tvTabSplit)).setTextColor(getResources().getColor(R.color.muted_fg));

        layoutRingkasan.setVisibility(View.VISIBLE);
        layoutSplit.setVisibility(View.GONE);
    }

    private void showSplit() {
        btnSplit.setBackgroundResource(R.drawable.btn_lime);
        ((TextView)findViewById(R.id.tvTabSplit)).setTextColor(getResources().getColor(R.color.bg));

        btnRingkasan.setBackground(null);
        ((TextView)findViewById(R.id.tvTabRingkasan)).setTextColor(getResources().getColor(R.color.muted_fg));

        layoutRingkasan.setVisibility(View.GONE);
        layoutSplit.setVisibility(View.VISIBLE);
    }

    private void setupCharts() {
        // ... (keep current mock as fallback if needed)
    }

    private void shareActivity() {
        Intent intent = new Intent(this, ActivityShareActivity.class);
        if (activityData != null) {
            intent.putExtra("ACTIVITY_DATA", activityData);
        } else {
            intent.putExtra("jarak", ((TextView) findViewById(R.id.tvDetailJarak)).getText().toString());
            intent.putExtra("elev", ((TextView) findViewById(R.id.tvDetailElev)).getText().toString());
            intent.putExtra("waktu", ((TextView) findViewById(R.id.tvDetailWaktu)).getText().toString());
            intent.putExtra("pace", ((TextView) findViewById(R.id.tvDetailPace)).getText().toString());
        }
        startActivity(intent);
    }
}
