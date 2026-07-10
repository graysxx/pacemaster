package com.example.pace.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class BarChartView extends View {

    private Paint barPaint, labelPaint;

    // Data: km per hari (Sen-Min), Sen=0 karena libur
    private float[] data = {0f, 5.2f, 7.8f, 3.4f, 6.5f, 9.2f, 4.1f};
    private String[] labels = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setColor(Color.parseColor("#C8F43A"));

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(Color.parseColor("#76767F"));
        labelPaint.setTextSize(11f * getResources().getDisplayMetrics().density);
        labelPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();
        float labelHeight = 24f * getResources().getDisplayMetrics().density;
        float chartH = h - labelHeight;

        float maxVal = 0;
        for (float d : data) if (d > maxVal) maxVal = d;

        float barW = (w / data.length) * 0.5f;
        float gap  = (w / data.length);

        for (int i = 0; i < data.length; i++) {
            float cx = gap * i + gap / 2f;
            float barH = data[i] == 0 ? 0 : (data[i] / maxVal) * (chartH * 0.85f);

            // Bar — tidak ada jika 0
            if (data[i] > 0) {
                // Lime untuk bar aktif, lebih terang untuk yang tertinggi
                if (data[i] == maxVal) {
                    barPaint.setColor(Color.parseColor("#C8F43A"));
                } else {
                    barPaint.setColor(Color.parseColor("#9BC42E"));
                }

                RectF rect = new RectF(
                        cx - barW / 2f,
                        chartH - barH,
                        cx + barW / 2f,
                        chartH
                );
                float radius = barW * 0.3f;
                canvas.drawRoundRect(rect, radius, radius, barPaint);
            }

            // Label hari
            canvas.drawText(labels[i], cx, h - 4f, labelPaint);
        }
    }
}