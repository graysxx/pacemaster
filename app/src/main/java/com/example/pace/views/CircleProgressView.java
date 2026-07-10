package com.example.pace.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircleProgressView extends View {

    private Paint trackPaint, progressPaint, textPaint;
    private float progress = 0.90f; // 90%
    private RectF oval;

    public CircleProgressView(Context context) {
        super(context);
        init();
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(8f);
        trackPaint.setColor(Color.parseColor("#2A2A2E"));
        trackPaint.setStrokeCap(Paint.Cap.ROUND);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(8f);
        progressPaint.setColor(Color.parseColor("#C8F43A"));
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#F0F0F5"));
        textPaint.setTextSize(16f * getResources().getDisplayMetrics().density);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth(), h = getHeight();
        float pad = 16f;
        oval = new RectF(pad, pad, w - pad, h - pad);

        // Track
        canvas.drawOval(oval, trackPaint);

        // Progress arc, sweep = progress * 360
        canvas.drawArc(oval, -90f, progress * 360f, false, progressPaint);

        // Teks persentase
        float textY = h / 2f - (textPaint.descent() + textPaint.ascent()) / 2f;
        canvas.drawText((int)(progress * 100) + "%", w / 2f, textY, textPaint);
    }

    public void setProgress(float p) {
        this.progress = p;
        invalidate();
    }
}