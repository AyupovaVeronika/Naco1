package com.example.myapplication.redactor;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplication.R;

public class ToolsPanel extends LinearLayout {
    private Button selectTool;
    private Button rectangleTool;
    private Button circleTool;
    private Button textTool;
    private Button handTool;
    private Button zoomIn;
    private Button zoomOut;

    private OnToolSelectedListener listener;
    private String currentTool = TOOL_SELECT;
    private Button selectedButton;

    public static final String TOOL_SELECT = "select";
    public static final String TOOL_RECTANGLE = "rectangle";
    public static final String TOOL_CIRCLE = "circle";
    public static final String TOOL_TEXT = "text";
    public static final String TOOL_HAND = "hand";

    public ToolsPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        setPadding(8, 16, 8, 16);
        setBackgroundColor(Color.parseColor("#2C3E50"));

        // Создаем кнопки с русскими подсказками
        selectTool = createToolButton("🖱️", getResources().getString(R.string.tool_select), TOOL_SELECT);
        rectangleTool = createToolButton("⬛", getResources().getString(R.string.tool_rectangle), TOOL_RECTANGLE);
        circleTool = createToolButton("⭕", getResources().getString(R.string.tool_circle), TOOL_CIRCLE);
        textTool = createToolButton("📝", getResources().getString(R.string.tool_text), TOOL_TEXT);

        addView(createDivider());

        handTool = createToolButton("✋", getResources().getString(R.string.tool_hand), TOOL_HAND);
        zoomIn = createToolButton("➕", getResources().getString(R.string.tool_zoom_in), "zoom_in");
        zoomOut = createToolButton("➖", getResources().getString(R.string.tool_zoom_out), "zoom_out");

        // Устанавливаем начальное выделение
        setCurrentTool(TOOL_SELECT);
    }

    private Button createToolButton(String symbol, String description, final String toolId) {
        Button button = new Button(getContext());
        button.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        ));
        button.setText(symbol);
        button.setTextSize(20);
        button.setPadding(12, 16, 12, 16);
        button.setBackgroundResource(android.R.drawable.list_selector_background);
        button.setTextColor(Color.WHITE);
        button.setGravity(Gravity.CENTER);

        button.setOnClickListener(v -> {
            if (toolId.equals("zoom_in")) {
                if (listener != null) listener.onZoomIn();
                Toast.makeText(getContext(), R.string.tool_zoom_in, Toast.LENGTH_SHORT).show();
            } else if (toolId.equals("zoom_out")) {
                if (listener != null) listener.onZoomOut();
                Toast.makeText(getContext(), R.string.tool_zoom_out, Toast.LENGTH_SHORT).show();
            } else {
                setCurrentTool(toolId);
                if (listener != null) listener.onToolSelected(toolId);
                Toast.makeText(getContext(), description, Toast.LENGTH_SHORT).show();
            }
        });

        addView(button);
        return button;
    }

    private View createDivider() {
        View divider = new View(getContext());
        divider.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                1
        ));
        divider.setBackgroundColor(Color.parseColor("#3D566E"));
        return divider;
    }

    private void setCurrentTool(String tool) {
        this.currentTool = tool;
        resetButtonStyles();

        Button buttonToSelect = null;
        switch (tool) {
            case TOOL_SELECT: buttonToSelect = selectTool; break;
            case TOOL_RECTANGLE: buttonToSelect = rectangleTool; break;
            case TOOL_CIRCLE: buttonToSelect = circleTool; break;
            case TOOL_TEXT: buttonToSelect = textTool; break;
            case TOOL_HAND: buttonToSelect = handTool; break;
        }

        if (buttonToSelect != null) {
            buttonToSelect.setBackgroundColor(Color.parseColor("#3498DB"));
            buttonToSelect.setTextColor(Color.WHITE);
            selectedButton = buttonToSelect;
        }
    }

    private void resetButtonStyles() {
        Button[] buttons = {selectTool, rectangleTool, circleTool, textTool, handTool, zoomIn, zoomOut};
        for (Button btn : buttons) {
            if (btn != null) {
                btn.setBackgroundResource(android.R.drawable.list_selector_background);
                btn.setTextColor(Color.WHITE);
            }
        }
    }

    public String getCurrentTool() {
        return currentTool;
    }

    public interface OnToolSelectedListener {
        void onToolSelected(String tool);
        void onZoomIn();
        void onZoomOut();
    }

    public void setOnToolSelectedListener(OnToolSelectedListener listener) {
        this.listener = listener;
    }
}