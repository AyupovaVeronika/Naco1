package com.example.myapplication.redactor;

import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.UUID;

public abstract class DesignElement {
    protected float x, y;
    protected float width, height;
    protected float rotation;
    protected Paint paint;
    protected String id;
    private boolean isSelected = false;

    protected int zIndex;

    // Новые поля для слоев
    private boolean isVisible = true;
    private boolean isLocked = false;
    private int opacity = 255;

    public DesignElement(float x, float y) {
        this.x = x;
        this.y = y;
        this.id = UUID.randomUUID().toString();
        this.paint = new Paint();
        initPaint();
    }

    protected abstract void initPaint();
    public abstract void draw(Canvas canvas);
    public abstract boolean contains(float touchX, float touchY);

    public void translate(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }

    public void scale(float scale) {
        this.width *= scale;
        this.height *= scale;
    }

    // Геттеры и сеттеры
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public float getWidth() { return width; }
    public void setWidth(float width) { this.width = width; }

    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }

    public float getRotation() { return rotation; }
    public void setRotation(float rotation) { this.rotation = rotation; }

    public int getZIndex() { return zIndex; }
    public void setZIndex(int zIndex) { this.zIndex = zIndex; }

    public String getId() { return id; }

    // Методы для слоев
    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { isVisible = visible; }

    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean locked) { isLocked = locked; }

    public int getOpacity() { return opacity; }
    public void setOpacity(int opacity) { this.opacity = opacity; }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}