package com.example.pace.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import com.example.pace.R;

public class LineChartView extends View {

    private Paint linePaint, fillPaint, labelPaint, gridPaint;
    private int chartColor = Color.parseColor("#C8F43A");

    private float[] data = {5.6f, 5.3f, 5.5f, 5.1f, 5.4f, 5.7f, 5.3f, 5.1f, 5.2f, 5.3f};
    private String[] xLabels = {"0.5", "1.0", "1.6", "2.1", "2.6", "3.1", "3.6", "4.2", "4.7", "5.2"};
    private String[] yLabels = {"5.75", "5.25", "4.75"};

    public LineChartView(Context context) {
        super(context);
        init(context, null);
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context ctx, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.LineChartView);
            chartColor = a.getColor(R.styleable.LineChartView_chartColor, chartColor);
            a.recycle();
        }

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(chartColor);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2f * ctx.getResources().getDisplayMetrics().density);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(Color.parseColor("#76767F"));
        labelPaint.setTextSize(10f * ctx.getResources().getDisplayMetrics().density);
        labelPaint.setTextAlign(Paint.Align.LEFT);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#1AFFFFFF"));
        gridPaint.setStrokeWidth(1f);
    }

    public void setData(float[] data, String[] xLabels, String[] yLabels, int color) {
        this.data = data;
        this.xLabels = xLabels;
        this.yLabels = yLabels;
        this.chartColor = color;
        linePaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();
        float labelPaddingBottom = 20f * getResources().getDisplayMetrics().density;
        float labelPaddingLeft = 30f * getResources().getDisplayMetrics().density;
        float chartW = w - labelPaddingLeft;
        float chartH = h - labelPaddingBottom;

        if (data == null || data.length == 0) return;

        // Find min/max for scaling
        float minVal = Float.MAX_VALUE;
        float maxVal = Float.MIN_VALUE;
        for (float d : data) {
            minVal = Math.min(minVal, d);
            maxVal = Math.max(maxVal, d);
        }
        
        // Manual Y bounds based on labels if possible, or use data
        float yMin = minVal * 0.95f;
        float yMax = maxVal * 1.05f;
        float yRange = yMax - yMin;

        // Draw Y Labels
        labelPaint.setTextAlign(Paint.Align.LEFT);
        for (int i = 0; i < yLabels.length; i++) {
            float yPos = (chartH / (yLabels.length - 1)) * i;
            canvas.drawText(yLabels[i], 0, yPos + 10, labelPaint);
        }

        // Calculate points
        float[] xs = new float[data.length];
        float[] ys = new float[data.length];
        float stepX = chartW / (data.length - 1);

        for (int i = 0; i < data.length; i++) {
            xs[i] = labelPaddingLeft + i * stepX;
            // Invert Y: higher value = lower on screen (usually, but for pace lower is better)
            // Looking at the mockup, the line goes up and down.
            // Let's assume higher value is higher on screen for elevation/cadence.
            // For pace, the mockup shows 5.75 at top and 4.75 at bottom? No, usually 4.75 is "better" so it's at top.
            // Wait, the mockup says 5.75 (top), 5.25 (middle), 4.75 (bottom). 
            // So higher number is at the top.
            ys[i] = chartH - ((data[i] - yMin) / yRange) * chartH;
        }

        // Draw Fill
        Path fillPath = new Path();
        fillPath.moveTo(xs[0], chartH);
        for (int i = 0; i < data.length; i++) {
            fillPath.lineTo(xs[i], ys[i]);
        }
        fillPath.lineTo(xs[data.length - 1], chartH);
        fillPath.close();

        LinearGradient grad = new LinearGradient(
                0, 0, 0, chartH,
                new int[]{setAlpha(chartColor, 0.4f), setAlpha(chartColor, 0.0f)},
                null, Shader.TileMode.CLAMP);
        fillPaint.setShader(grad);
        canvas.drawPath(fillPath, fillPaint);

        // Draw Line
        Path linePath = new Path();
        linePath.moveTo(xs[0], ys[0]);
        for (int i = 1; i < data.length; i++) {
            linePath.lineTo(xs[i], ys[i]);
        }
        canvas.drawPath(linePath, linePaint);

        // Draw X Labels
        labelPaint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < xLabels.length; i++) {
            canvas.drawText(xLabels[i], xs[i], h - 5, labelPaint);
        }
    }

    private int setAlpha(int color, float alpha) {
        int a = Math.round(Color.alpha(color) * alpha);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(a, r, g, b);
    }
}
