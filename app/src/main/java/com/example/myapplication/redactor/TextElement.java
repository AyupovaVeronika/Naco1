package com.example.myapplication.redactor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class TextElement extends DesignElement {
    private String text;
    private float textSize;
    private int textColor;

    public TextElement(float x, float y, String text) {
        super(x, y);
        this.text = text;
        this.textSize = 40f;
        this.textColor = Color.BLACK;
        initPaint();
        measureText();
    }

    @Override
    protected void initPaint() {
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setAntiAlias(true);
    }

    private void measureText() {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        this.width = bounds.width();
        this.height = bounds.height();
    }

    @Override
    public void draw(Canvas canvas) {
        paint.setColor(textColor);
        canvas.drawText(text, x, y, paint); // Исправлено: y без +height
    }

    @Override
    public boolean contains(float touchX, float touchY) {
        return touchX >= x && touchX <= x + width &&
                touchY >= y - height && touchY <= y;
    }

    // ДОБАВЬТЕ ЭТОТ МЕТОД
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        measureText();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        initPaint();
        measureText();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        initPaint();
    }
}