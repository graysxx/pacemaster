package com.example.pace.views;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class CalendarDotsView extends View {

    private Paint activePaint, inactivePaint, textActive, textInactive;

    // Hari yang ada larinya di bulan Juni (true = lari, false = tidak)
    private final boolean[] runDays = {
            true,  true,  true,  false, true,  true,  true,   // 1-7
            true,  true,  true,  false, true,  true,  true,   // 8-14
            false, true,  true,  true,  false, true,  false,  // 15-21
            true,  true,  false, true,  true,  true,  false,  // 22-28
            true,  true                                        // 29-30
    };

    private final int cols = 7;
    private float dp;

    public CalendarDotsView(Context context) {
        super(context);
        init(context);
    }

    public CalendarDotsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarDotsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        dp = ctx.getResources().getDisplayMetrics().density;

        activePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        activePaint.setColor(Color.parseColor("#4A6B1A")); // hijau tua

        inactivePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        inactivePaint.setColor(Color.parseColor("#1A1A1D")); // gelap

        textActive = new Paint(Paint.ANTI_ALIAS_FLAG);
        textActive.setColor(Color.parseColor("#C8F43A"));
        textActive.setTextSize(13f * dp);
        textActive.setTextAlign(Paint.Align.CENTER);
        textActive.setFakeBoldText(true);

        textInactive = new Paint(Paint.ANTI_ALIAS_FLAG);
        textInactive.setColor(Color.parseColor("#76767F"));
        textInactive.setTextSize(13f * dp);
        textInactive.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int wSpec, int hSpec) {
        int w = MeasureSpec.getSize(wSpec);
        float cellSize = w / (float) cols;
        int rows = (int) Math.ceil(runDays.length / (float) cols);
        int h = (int)(rows * cellSize + 8 * dp);
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(@androidx.annotation.NonNull Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float cellSize = w / (float) cols;
        float radius = cellSize * 0.38f;

        for (int i = 0; i < runDays.length; i++) {
            int col = i % cols;
            int row = i / cols;

            float cx = col * cellSize + cellSize / 2f;
            float cy = row * cellSize + cellSize / 2f;

            boolean ran = runDays[i];

            // Lingkaran background
            canvas.drawCircle(cx, cy, radius, ran ? activePaint : inactivePaint);

            // Angka tanggal
            float textY = cy - (textActive.descent() + textActive.ascent()) / 2f;
            canvas.drawText(String.valueOf(i + 1), cx, textY, ran ? textActive : textInactive);
        }
    }
}