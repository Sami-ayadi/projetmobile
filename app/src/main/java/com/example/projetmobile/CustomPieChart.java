package com.example.projetmobile;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class CustomPieChart extends View {
    private List<Float> values; // Data values for the chart
    private List<Integer> colors; // Colors for each slice
    private Paint paint;
    private RectF rectF;

    public CustomPieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectF = new RectF();
    }

    /**
     * Sets the data for the pie chart.
     *
     * @param values List of values representing each slice's size.
     * @param colors List of colors for each slice.
     */
    public void setData(List<Float> values, List<Integer> colors) {
        this.values = values;
        this.colors = colors;
        invalidate(); // Redraw the view with the new data
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (values == null || values.isEmpty()) return;

        float total = 0;
        for (Float value : values) {
            total += value;
        }

        int width = getWidth();
        int height = getHeight();
        float radius = Math.min(width, height) / 2f;
        rectF.set(width / 2f - radius, height / 2f - radius, width / 2f + radius, height / 2f + radius);

        float startAngle = 0f;
        for (int i = 0; i < values.size(); i++) {
            float sweepAngle = (values.get(i) / total) * 360f;

            paint.setColor(colors.get(i));
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);

            startAngle += sweepAngle;
        }
    }
}
