package com.example.myapplication.redactor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CanvasView extends View {
    // Матрица трансформации для масштабирования и перемещения
    private Matrix transformMatrix = new Matrix();
    private float scaleFactor = 1.0f;
    private float minScale = 0.1f;
    private float maxScale = 5.0f;

    // Список элементов на холсте
    private List<DesignElement> elements = new ArrayList<>();

    // Выбранный элемент
    private DesignElement selectedElement;

    // Кисти для рисования
    private Paint gridPaint;
    private Paint backgroundPaint;
    private Paint selectionPaint;

    // Размер сетки
    private static final float GRID_SIZE = 50f;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Настройка фона
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);

        // Настройка сетки
        gridPaint = new Paint();
        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setStrokeWidth(1f);

        // Настройка рамки выделения
        selectionPaint = new Paint();
        selectionPaint.setColor(Color.BLUE);
        selectionPaint.setStyle(Paint.Style.STROKE);
        selectionPaint.setStrokeWidth(2f);

        // Включаем рисование
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Сохраняем состояние canvas
        canvas.save();

        // Применяем трансформации (масштаб, перемещение)
        canvas.concat(transformMatrix);

        // Рисуем бесконечную сетку
        drawInfiniteGrid(canvas);

        // Сортируем элементы по zIndex (от меньшего к большему)
        List<DesignElement> sortedElements = new ArrayList<>(elements);
        sortedElements.sort((e1, e2) -> Integer.compare(e1.getZIndex(), e2.getZIndex()));

        // Рисуем все элементы
        for (DesignElement element : sortedElements) {
            element.draw(canvas);
        }

        // Рисуем рамку вокруг выбранного элемента
        if (selectedElement != null) {
            drawSelectionBorder(canvas, selectedElement);
        }

        // Восстанавливаем состояние
        canvas.restore();
    }

    private void drawInfiniteGrid(Canvas canvas) {
        // Получаем текущие границы видимой области в координатах холста
        RectF visibleRect = getVisibleRect();

        // Рисуем горизонтальные линии
        float startY = (float) Math.floor(visibleRect.top / GRID_SIZE) * GRID_SIZE;
        float endY = (float) Math.ceil(visibleRect.bottom / GRID_SIZE) * GRID_SIZE;

        for (float y = startY; y <= endY; y += GRID_SIZE) {
            canvas.drawLine(visibleRect.left, y, visibleRect.right, y, gridPaint);
        }

        // Рисуем вертикальные линии
        float startX = (float) Math.floor(visibleRect.left / GRID_SIZE) * GRID_SIZE;
        float endX = (float) Math.ceil(visibleRect.right / GRID_SIZE) * GRID_SIZE;

        for (float x = startX; x <= endX; x += GRID_SIZE) {
            canvas.drawLine(x, visibleRect.top, x, visibleRect.bottom, gridPaint);
        }
    }

    private void drawSelectionBorder(Canvas canvas, DesignElement element) {
        // Сохраняем текущую матрицу
        canvas.save();

        // Применяем трансформации элемента (если есть rotation)
        // canvas.rotate(element.getRotation(), element.getX() + element.getWidth()/2,
        //              element.getY() + element.getHeight()/2);

        // Рисуем рамку в зависимости от типа элемента
        if (element instanceof RectangleElement) {
            RectangleElement rect = (RectangleElement) element;
            canvas.drawRect(
                    rect.getX() - 5,
                    rect.getY() - 5,
                    rect.getX() + rect.getWidth() + 5,
                    rect.getY() + rect.getHeight() + 5,
                    selectionPaint
            );
        } else if (element instanceof TextElement) {
            TextElement text = (TextElement) element;
            canvas.drawRect(
                    text.getX() - 5,
                    text.getY() - text.getHeight() - 5,
                    text.getX() + text.getWidth() + 5,
                    text.getY() + 5,
                    selectionPaint
            );
        }

        // Восстанавливаем матрицу
        canvas.restore();
    }

    private RectF getVisibleRect() {
        RectF rect = new RectF();
        Matrix inverse = new Matrix();
        transformMatrix.invert(inverse);
        rect.set(0, 0, getWidth(), getHeight());
        inverse.mapRect(rect);
        return rect;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handleTouch(event);
        return true;
    }

    public void handleTouch(MotionEvent event) {
        // Преобразуем координаты касания в координаты холста
        float[] touchPoint = new float[]{event.getX(), event.getY()};
        Matrix inverse = new Matrix();
        transformMatrix.invert(inverse);
        inverse.mapPoints(touchPoint);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Проверяем, попали ли в какой-либо элемент
                selectedElement = findElementAt(touchPoint[0], touchPoint[1]);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                if (selectedElement != null) {
                    // Сохраняем старую позицию для undo
                    float oldX = selectedElement.getX();
                    float oldY = selectedElement.getY();

                    // Перемещаем выбранный элемент
                    selectedElement.translate(
                            touchPoint[0] - selectedElement.getX(),
                            touchPoint[1] - selectedElement.getY()
                    );
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                // Действие при отпускании
                break;
        }
    }

    private DesignElement findElementAt(float x, float y) {
        // Ищем с последнего элемента (верхний слой)
        for (int i = elements.size() - 1; i >= 0; i--) {
            DesignElement element = elements.get(i);
            if (element.contains(x, y)) {
                return element;
            }
        }
        return null;
    }

    // Методы для масштабирования
    public void zoomIn() {
        scaleFactor *= 1.2f;
        if (scaleFactor > maxScale) scaleFactor = maxScale;
        transformMatrix.setScale(scaleFactor, scaleFactor, getWidth() / 2f, getHeight() / 2f);
        invalidate();
    }

    public void zoomOut() {
        scaleFactor *= 0.8f;
        if (scaleFactor < minScale) scaleFactor = minScale;
        transformMatrix.setScale(scaleFactor, scaleFactor, getWidth() / 2f, getHeight() / 2f);
        invalidate();
    }

    public void scale(float factor) {
        scaleFactor *= factor;
        if (scaleFactor < minScale) scaleFactor = minScale;
        if (scaleFactor > maxScale) scaleFactor = maxScale;
        transformMatrix.postScale(factor, factor, getWidth() / 2f, getHeight() / 2f);
        invalidate();
    }

    public void pan(float dx, float dy) {
        transformMatrix.postTranslate(dx, dy);
        invalidate();
    }

    // Геттеры и вспомогательные методы
    public float getViewportCenterX() {
        return getVisibleRect().centerX();
    }

    public float getViewportCenterY() {
        return getVisibleRect().centerY();
    }

    public float[] screenToCanvas(float screenX, float screenY) {
        float[] point = new float[]{screenX, screenY};
        Matrix inverse = new Matrix();
        transformMatrix.invert(inverse);
        inverse.mapPoints(point);
        return point;
    }

    public void setSelectedElement(DesignElement element) {
        this.selectedElement = element;
        invalidate();
    }

    public DesignElement getSelectedElement() {
        return selectedElement;
    }

    public List<DesignElement> getElements() {
        return elements;
    }

    public void setElements(List<DesignElement> elements) {
        this.elements = elements;
        invalidate();
    }

    public void addElement(DesignElement element) {
        // Устанавливаем zIndex для нового элемента (поверх всех)
        int maxZIndex = elements.stream()
                .mapToInt(DesignElement::getZIndex)
                .max()
                .orElse(0);
        element.setZIndex(maxZIndex + 1);

        elements.add(element);
        invalidate();
    }

    public void removeElement(DesignElement element) {
        elements.remove(element);
        if (selectedElement == element) {
            selectedElement = null;
        }
        invalidate();
    }

    public void clear() {
        elements.clear();
        selectedElement = null;
        invalidate();
    }

    // Методы для работы со слоями
    public void bringToFront(DesignElement element) {
        int maxZIndex = elements.stream()
                .mapToInt(DesignElement::getZIndex)
                .max()
                .orElse(0);
        element.setZIndex(maxZIndex + 1);
        invalidate();
    }

    public void sendToBack(DesignElement element) {
        int minZIndex = elements.stream()
                .mapToInt(DesignElement::getZIndex)
                .min()
                .orElse(0);
        element.setZIndex(minZIndex - 1);
        invalidate();
    }
    public List<DesignElement> getSelectedElements() {
        List<DesignElement> selected = new ArrayList<>();
        for (DesignElement element : elements) {
            if (element.isSelected()) {
                selected.add(element);
            }
        }
        return selected;
    }

    public void clearSelection() {
        for (DesignElement element : elements) {
            element.setSelected(false);
        }
        selectedElement = null;
        invalidate();
    }
}