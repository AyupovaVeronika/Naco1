package com.example.myapplication.redactor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class CircleElement extends DesignElement {
    private int color;
    private float radius;

    public CircleElement(float x, float y, float radius, int color) {
        super(x, y);
        this.radius = radius;
        this.width = radius * 2;
        this.height = radius * 2;
        this.color = color;
        initPaint();
    }

    @Override
    protected void initPaint() {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        paint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(x + radius, y + radius, radius, paint);
    }

    @Override
    public boolean contains(float touchX, float touchY) {
        float centerX = x + radius;
        float centerY = y + radius;
        float dx = touchX - centerX;
        float dy = touchY - centerY;
        return (dx * dx + dy * dy) <= radius * radius;
    }

    public void setColor(int color) {
        this.color = color;
        initPaint();
    }

    public int getColor() {
        return color;
    }
}