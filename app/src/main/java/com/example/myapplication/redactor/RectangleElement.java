package com.example.myapplication.redactor;

import android.graphics.Canvas;
import android.graphics.Paint;

public class RectangleElement extends DesignElement {
    private int color;
    private int strokeColor;
    private float strokeWidth;
    private float cornerRadius;

    public RectangleElement(float x, float y, float width, float height, int color) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.color = color;
        this.strokeWidth = 0;
        this.cornerRadius = 0;
    }

    @Override
    protected void initPaint() {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        paint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas) {
        // Рисуем заливку
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawRoundRect(x, y, x + width, y + height, cornerRadius, cornerRadius, paint);

        // Рисуем обводку если есть
        if (strokeWidth > 0) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(strokeColor);
            paint.setStrokeWidth(strokeWidth);
            canvas.drawRoundRect(x, y, x + width, y + height, cornerRadius, cornerRadius, paint);
        }
    }

    @Override
    public boolean contains(float touchX, float touchY) {
        return touchX >= x && touchX <= x + width &&
                touchY >= y && touchY <= y + height;
    }

    // ДОБАВЛЯЕМ НЕДОСТАЮЩИЕ МЕТОДЫ
    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        initPaint();
    }

    public void setStroke(int color, float width) {
        this.strokeColor = color;
        this.strokeWidth = width;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}