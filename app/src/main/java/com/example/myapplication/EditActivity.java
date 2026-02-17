package com.example.myapplication;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.example.myapplication.redactor.CanvasView;
import com.example.myapplication.redactor.CircleElement;
import com.example.myapplication.redactor.DesignElement;
import com.example.myapplication.redactor.LayersPanel;
import com.example.myapplication.redactor.PropertiesPanel;
import com.example.myapplication.redactor.RectangleElement;
import com.example.myapplication.redactor.TextElement;
import com.example.myapplication.redactor.ToolsPanel;
import com.example.myapplication.redactor.UndoRedoManager;

import java.util.List;

public class EditActivity extends AppCompatActivity {

    private CanvasView canvasView;
    private ToolsPanel toolsPanel;
    private LayersPanel layersPanel;
    private PropertiesPanel propertiesPanel;
    private UndoRedoManager undoRedoManager;

    // UI элементы для правой панели
    private LinearLayout rightPanel;
    private View rightDivider;
    private ImageButton btnToggleRightPanel;
    private ImageButton btnCloseRightPanel;

    private boolean isRightPanelOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_edit);

        initViews();
        setupPanels();
        setupGestures();
        setupToggleButtons();
        loadProject();

        // Добавляем тестовый элемент
        addTestElement();
    }

    private void initViews() {
        canvasView = findViewById(R.id.canvas_view);
        toolsPanel = findViewById(R.id.tools_panel);

        // Панели сворачивания
        rightPanel = findViewById(R.id.right_panel);
        rightDivider = findViewById(R.id.right_divider);
        btnToggleRightPanel = findViewById(R.id.btn_toggle_right_panel);
        btnCloseRightPanel = findViewById(R.id.btn_close_right_panel);

        // Панели слоев и свойств (они уже есть в XML)
        layersPanel = findViewById(R.id.layers_panel);
        propertiesPanel = findViewById(R.id.properties_panel);

        // Инициализация менеджера истории
        undoRedoManager = new UndoRedoManager(canvasView.getElements());
    }

    private void setupToggleButtons() {
        // Кнопка открытия правой панели
        btnToggleRightPanel.setOnClickListener(v -> {
            rightPanel.setVisibility(View.VISIBLE);
            rightDivider.setVisibility(View.VISIBLE);
            btnToggleRightPanel.setVisibility(View.GONE);
            isRightPanelOpen = true;
        });

        // Кнопка закрытия правой панели
        btnCloseRightPanel.setOnClickListener(v -> {
            rightPanel.setVisibility(View.GONE);
            rightDivider.setVisibility(View.GONE);
            btnToggleRightPanel.setVisibility(View.VISIBLE);
            isRightPanelOpen = false;
        });
    }

    private void setupPanels() {
        // Панель инструментов
        toolsPanel.setOnToolSelectedListener(new ToolsPanel.OnToolSelectedListener() {
            @Override
            public void onToolSelected(String tool) {
                Toast.makeText(EditActivity.this,
                        getToolNameInRussian(tool), Toast.LENGTH_SHORT).show();
                handleToolSelection(tool);
            }

            @Override
            public void onZoomIn() {
                canvasView.zoomIn();
            }

            @Override
            public void onZoomOut() {
                canvasView.zoomOut();
            }
        });

        // Панель слоев
        layersPanel.setOnLayerActionListener(new LayersPanel.OnLayerActionListener() {
            @Override
            public void onLayerSelected(DesignElement element) {
                canvasView.setSelectedElement(element);
                propertiesPanel.setElement(element);
            }

            @Override
            public void onDelete() {
                if (canvasView.getSelectedElement() != null) {
                    canvasView.removeElement(canvasView.getSelectedElement());
                    layersPanel.updateLayers(canvasView.getElements());
                    propertiesPanel.setElement(null);
                }
            }

            @Override
            public void onLayerVisibilityToggled(DesignElement element) {
                element.setVisible(!element.isVisible());
                canvasView.invalidate();
                layersPanel.updateLayers(canvasView.getElements());
            }

            @Override
            public void onLayerLockedToggled(DesignElement element) {
                element.setLocked(!element.isLocked());
                // Можно показать Toast
                Toast.makeText(EditActivity.this,
                        element.isLocked() ? "Слой заблокирован" : "Слой разблокирован",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGroup() {
                Toast.makeText(EditActivity.this, "Группировка", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUngroup() {
                Toast.makeText(EditActivity.this, "Разгруппировка", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDuplicate() {
                duplicateSelectedElement();
            }

            @Override
            public void onLayerMoved(DesignElement element, int newPosition) {
                // Перемещение слоя (изменение порядка)
                List<DesignElement> elements = canvasView.getElements();
                elements.remove(element);
                elements.add(newPosition, element);

                // Обновляем zIndex для всех элементов
                for (int i = 0; i < elements.size(); i++) {
                    elements.get(i).setZIndex(i);
                }

                canvasView.invalidate();
                layersPanel.updateLayers(elements);
            }
        });

        // Панель свойств
        propertiesPanel.setOnPropertyChangedListener(new PropertiesPanel.OnPropertyChangedListener() {
            @Override
            public void onColorChanged(DesignElement element, int newColor) {
                if (element instanceof RectangleElement) {
                    ((RectangleElement) element).setColor(newColor);
                    canvasView.invalidate();
                } else if (element instanceof CircleElement) {
                    ((CircleElement) element).setColor(newColor);
                    canvasView.invalidate();
                }
            }

            @Override
            public void onPositionChanged(DesignElement element, float x, float y) {
                element.setX(x);
                element.setY(y);
                canvasView.invalidate();
            }

            @Override
            public void onSizeChanged(DesignElement element, float width, float height) {
                if (element instanceof RectangleElement) {
                    ((RectangleElement) element).setWidth(width);
                    ((RectangleElement) element).setHeight(height);
                    canvasView.invalidate();
                } else if (element instanceof CircleElement) {
                    // Для круга ширина и высота - это диаметр
                    ((CircleElement) element).setWidth(width);
                    ((CircleElement) element).setHeight(height);
                    canvasView.invalidate();
                }
            }

            @Override
            public void onRotationChanged(DesignElement element, float rotation) {
                element.setRotation(rotation);
                canvasView.invalidate();
            }

            @Override
            public void onOpacityChanged(DesignElement element, int opacity) {
                // Реализация прозрачности
                element.setOpacity(opacity);
                canvasView.invalidate();
            }

            @Override
            public void onTextChanged(TextElement element, String newText) {
                element.setText(newText);
                canvasView.invalidate();
            }
        });
    }

    private void duplicateSelectedElement() {
        DesignElement selected = canvasView.getSelectedElement();
        if (selected == null) {
            Toast.makeText(this, "Нет выбранного элемента", Toast.LENGTH_SHORT).show();
            return;
        }

        // Создаем копию элемента
        if (selected instanceof RectangleElement) {
            RectangleElement rect = (RectangleElement) selected;
            RectangleElement copy = new RectangleElement(
                    rect.getX() + 20, rect.getY() + 20,
                    rect.getWidth(), rect.getHeight(),
                    rect.getColor()
            );
            canvasView.addElement(copy);
        } else if (selected instanceof CircleElement) {
            CircleElement circle = (CircleElement) selected;
            CircleElement copy = new CircleElement(
                    circle.getX() + 20, circle.getY() + 20,
                    circle.getWidth() / 2, circle.getColor()
            );
            canvasView.addElement(copy);
        } else if (selected instanceof TextElement) {
            TextElement text = (TextElement) selected;
            TextElement copy = new TextElement(
                    text.getX() + 20, text.getY() + 20,
                    text.getText()
            );
            canvasView.addElement(copy);
        }

        layersPanel.updateLayers(canvasView.getElements());
        Toast.makeText(this, "Элемент дублирован", Toast.LENGTH_SHORT).show();
    }

    private String getToolNameInRussian(String tool) {
        switch (tool) {
            case ToolsPanel.TOOL_SELECT: return "Выбрать";
            case ToolsPanel.TOOL_RECTANGLE: return "Прямоугольник";
            case ToolsPanel.TOOL_CIRCLE: return "Круг";
            case ToolsPanel.TOOL_TEXT: return "Текст";
            case ToolsPanel.TOOL_HAND: return "Рука";
            default: return tool;
        }
    }

    private void setupGestures() {
        GestureDetectorCompat gestureDetector = new GestureDetectorCompat(this, new GestureListener());
        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        canvasView.setOnTouchListener((v, event) -> {
            boolean result = scaleGestureDetector.onTouchEvent(event);
            result = gestureDetector.onTouchEvent(event) || result;
            canvasView.handleTouch(event);
            return result;
        });
    }

    private void handleToolSelection(String tool) {
        switch (tool) {
            case ToolsPanel.TOOL_RECTANGLE:
                addRectangle();
                break;
            case ToolsPanel.TOOL_CIRCLE:
                addCircle();
                break;
            case ToolsPanel.TOOL_TEXT:
                addText();
                break;
        }
    }

    private void addRectangle() {
        float x = canvasView.getViewportCenterX();
        float y = canvasView.getViewportCenterY();

        RectangleElement rect = new RectangleElement(x - 50, y - 50, 100, 100, Color.BLUE);
        canvasView.addElement(rect);
        layersPanel.updateLayers(canvasView.getElements());

        Toast.makeText(this, R.string.rectangle_added, Toast.LENGTH_SHORT).show();

        // Автоматически открываем правую панель
        if (!isRightPanelOpen) {
            btnToggleRightPanel.performClick();
        }
    }

    private void addCircle() {
        float x = canvasView.getViewportCenterX();
        float y = canvasView.getViewportCenterY();

        CircleElement circle = new CircleElement(x - 50, y - 50, 50, Color.RED);
        canvasView.addElement(circle);
        layersPanel.updateLayers(canvasView.getElements());

        Toast.makeText(this, R.string.circle_added, Toast.LENGTH_SHORT).show();
    }

    private void addText() {
        float x = canvasView.getViewportCenterX();
        float y = canvasView.getViewportCenterY();

        TextElement text = new TextElement(x, y, getString(R.string.sample_text));
        canvasView.addElement(text);
        layersPanel.updateLayers(canvasView.getElements());

        Toast.makeText(this, R.string.text_added, Toast.LENGTH_SHORT).show();
    }

    private void addTestElement() {
        RectangleElement rect = new RectangleElement(200, 200, 150, 150, Color.GREEN);
        canvasView.addElement(rect);
        layersPanel.updateLayers(canvasView.getElements());
    }

    private void loadProject() {
        String filePath = getIntent().getStringExtra("file_path");
        if (filePath != null) {
            Toast.makeText(this, "Загрузка: " + filePath, Toast.LENGTH_SHORT).show();
            // TODO: Загрузить проект из файла
        }
    }

    // Метод для контекстного меню слоя
    public void showLayerContextMenu(DesignElement element) {
        String[] options = {"На передний план", "На задний план", "Дублировать", "Удалить"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Действия со слоем")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // На передний план
                            bringToFront(element);
                            break;
                        case 1: // На задний план
                            sendToBack(element);
                            break;
                        case 2: // Дублировать
                            duplicateSelectedElement();
                            break;
                        case 3: // Удалить
                            canvasView.removeElement(element);
                            layersPanel.updateLayers(canvasView.getElements());
                            propertiesPanel.setElement(null);
                            break;
                    }
                })
                .show();
    }

    private void bringToFront(DesignElement element) {
        List<DesignElement> elements = canvasView.getElements();
        int maxZIndex = elements.stream()
                .mapToInt(DesignElement::getZIndex)
                .max()
                .orElse(0);
        element.setZIndex(maxZIndex + 1);
        canvasView.invalidate();
        layersPanel.updateLayers(elements);
    }

    private void sendToBack(DesignElement element) {
        List<DesignElement> elements = canvasView.getElements();
        int minZIndex = elements.stream()
                .mapToInt(DesignElement::getZIndex)
                .min()
                .orElse(0);
        element.setZIndex(minZIndex - 1);
        canvasView.invalidate();
        layersPanel.updateLayers(elements);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float[] point = canvasView.screenToCanvas(e.getX(), e.getY());

            if (toolsPanel.getCurrentTool().equals(ToolsPanel.TOOL_RECTANGLE)) {
                RectangleElement rect = new RectangleElement(point[0] - 50, point[1] - 50, 100, 100, Color.MAGENTA);
                canvasView.addElement(rect);
                layersPanel.updateLayers(canvasView.getElements());
            }
            return true;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            canvasView.scale(detector.getScaleFactor());
            return true;
        }
    }
}