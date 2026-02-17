package com.example.myapplication.redactor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myapplication.R;

public class PropertiesPanel extends LinearLayout {
    private DesignElement currentElement;
    private OnPropertyChangedListener listener;

    // Компактные элементы управления
    private View colorPreview;
    private SeekBar opacityBar;
    private EditText widthInput;
    private EditText heightInput;
    private EditText xInput;
    private EditText yInput;
    private SeekBar rotationBar;
    private EditText textInput;
    private Spinner fontSpinner;
    private SeekBar textSizeBar;
    private CheckBox borderCheckbox;
    private EditText borderWidth;
    private View borderColorPreview;

    // Ссылка на секцию текста
    private View textSection;

    // Состояние сворачивания
    private boolean isExpanded = true;
    private LinearLayout contentContainer;

    public PropertiesPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        setBackgroundColor(Color.parseColor("#2C3E50"));

        // Заголовок с кнопкой сворачивания
        LinearLayout header = new LinearLayout(context);
        header.setOrientation(HORIZONTAL);
        header.setPadding(16, 12, 16, 12);
        header.setBackgroundColor(Color.parseColor("#34495E"));

        TextView title = new TextView(context);
        title.setText(R.string.properties);
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.WHITE);
        title.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));

        final TextView toggleButton = new TextView(context);
        toggleButton.setText("▼");
        toggleButton.setTextSize(16);
        toggleButton.setTextColor(Color.WHITE);
        toggleButton.setPadding(8, 0, 8, 0);
        toggleButton.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            contentContainer.setVisibility(isExpanded ? VISIBLE : GONE);
            toggleButton.setText(isExpanded ? "▼" : "▶");
        });

        header.addView(title);
        header.addView(toggleButton);
        addView(header);

        // Контейнер для контента (будет сворачиваться)
        contentContainer = new LinearLayout(context);
        contentContainer.setOrientation(VERTICAL);
        contentContainer.setPadding(12, 8, 12, 8);

        // Добавляем секции компактно
        addCompactSection(contentContainer, getResources().getString(R.string.position), createCompactPositionControls());
        addCompactSection(contentContainer, getResources().getString(R.string.size), createCompactSizeControls());
        addCompactSection(contentContainer, getResources().getString(R.string.appearance), createCompactAppearanceControls());
        addCompactSection(contentContainer, getResources().getString(R.string.border), createCompactBorderControls());
        addCompactSection(contentContainer, getResources().getString(R.string.rotation), createCompactRotationControls());

        // Секция текста - сохраняем ссылку на неё
        textSection = createCompactTextControls();
        textSection.setVisibility(GONE);
        contentContainer.addView(textSection);

        // Оборачиваем контент в ScrollView
        ScrollView scrollView = new ScrollView(context);
        scrollView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        ));
        scrollView.addView(contentContainer);
        addView(scrollView);
    }

    private void addCompactSection(LinearLayout container, String title, View content) {
        // Заголовок секции
        TextView sectionTitle = new TextView(getContext());
        sectionTitle.setText(title);
        sectionTitle.setTextSize(12);
        sectionTitle.setTypeface(null, Typeface.BOLD);
        sectionTitle.setTextColor(Color.parseColor("#FFB74D"));
        sectionTitle.setPadding(0, 8, 0, 4);
        container.addView(sectionTitle);

        // Контент секции
        container.addView(content);

        // Тонкий разделитель
        View divider = new View(getContext());
        divider.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, 1
        ));
        divider.setBackgroundColor(Color.parseColor("#3D566E"));
        container.addView(divider);
    }

    private View createCompactPositionControls() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(HORIZONTAL);
        layout.setPadding(0, 4, 0, 4);

        xInput = createCompactNumberInput("X");
        yInput = createCompactNumberInput("Y");

        layout.addView(xInput);
        layout.addView(createHorizontalSpacer(8));
        layout.addView(yInput);

        return layout;
    }

    private View createCompactSizeControls() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(HORIZONTAL);
        layout.setPadding(0, 4, 0, 4);

        widthInput = createCompactNumberInput("W");
        heightInput = createCompactNumberInput("H");

        layout.addView(widthInput);
        layout.addView(createHorizontalSpacer(8));
        layout.addView(heightInput);

        return layout;
    }

    private View createCompactAppearanceControls() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(VERTICAL);

        // Цвет в одну строку
        LinearLayout colorRow = new LinearLayout(getContext());
        colorRow.setOrientation(HORIZONTAL);
        colorRow.setPadding(0, 4, 0, 4);

        TextView colorLabel = new TextView(getContext());
        colorLabel.setText(R.string.color);
        colorLabel.setTextColor(Color.WHITE);
        colorLabel.setTextSize(12);
        colorLabel.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));

        colorPreview = new View(getContext());
        colorPreview.setLayoutParams(new LayoutParams(60, 30));
        colorPreview.setBackgroundColor(Color.BLUE);
        colorPreview.setOnClickListener(v -> openColorPicker());

        colorRow.addView(colorLabel);
        colorRow.addView(colorPreview);

        // Прозрачность в одну строку
        LinearLayout opacityRow = new LinearLayout(getContext());
        opacityRow.setOrientation(HORIZONTAL);
        opacityRow.setPadding(0, 4, 0, 4);

        TextView opacityLabel = new TextView(getContext());
        opacityLabel.setText(R.string.opacity);
        opacityLabel.setTextColor(Color.WHITE);
        opacityLabel.setTextSize(12);
        opacityLabel.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));

        opacityBar = new SeekBar(getContext());
        opacityBar.setMax(255);
        opacityBar.setProgress(255);
        opacityBar.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 2));

        opacityRow.addView(opacityLabel);
        opacityRow.addView(opacityBar);

        layout.addView(colorRow);
        layout.addView(opacityRow);

        return layout;
    }

    private View createCompactBorderControls() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(VERTICAL);

        borderCheckbox = new CheckBox(getContext());
        borderCheckbox.setText(R.string.enable_border);
        borderCheckbox.setTextColor(Color.WHITE);
        borderCheckbox.setTextSize(12);

        LinearLayout borderRow = new LinearLayout(getContext());
        borderRow.setOrientation(HORIZONTAL);
        borderRow.setPadding(0, 4, 0, 4);

        borderWidth = new EditText(getContext());
        borderWidth.setHint("W");
        borderWidth.setHintTextColor(Color.GRAY);
        borderWidth.setTextColor(Color.WHITE);
        borderWidth.setTextSize(12);
        borderWidth.setInputType(InputType.TYPE_CLASS_NUMBER);
        borderWidth.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        borderWidth.setPadding(8, 4, 8, 4);
        borderWidth.setBackgroundColor(Color.parseColor("#34495E"));

        borderColorPreview = new View(getContext());
        borderColorPreview.setLayoutParams(new LayoutParams(40, 30));
        borderColorPreview.setBackgroundColor(Color.BLACK);
        borderColorPreview.setOnClickListener(v -> openBorderColorPicker());

        borderRow.addView(borderWidth);
        borderRow.addView(createHorizontalSpacer(8));
        borderRow.addView(borderColorPreview);

        layout.addView(borderCheckbox);
        layout.addView(borderRow);

        return layout;
    }

    private View createCompactRotationControls() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(HORIZONTAL);
        layout.setPadding(0, 4, 0, 4);

        TextView rotationLabel = new TextView(getContext());
        rotationLabel.setText(R.string.angle);
        rotationLabel.setTextColor(Color.WHITE);
        rotationLabel.setTextSize(12);
        rotationLabel.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));

        rotationBar = new SeekBar(getContext());
        rotationBar.setMax(360);
        rotationBar.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 2));

        layout.addView(rotationLabel);
        layout.addView(rotationBar);

        return layout;
    }

    private View createCompactTextControls() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(VERTICAL);
        layout.setTag("text_section"); // Добавляем тег для идентификации

        // Text input
        textInput = new EditText(getContext());
        textInput.setHint(R.string.enter_text);
        textInput.setHintTextColor(Color.GRAY);
        textInput.setTextColor(Color.WHITE);
        textInput.setTextSize(12);
        textInput.setPadding(8, 8, 8, 8);
        textInput.setBackgroundColor(Color.parseColor("#34495E"));
        layout.addView(textInput);

        // Font spinner
        fontSpinner = new Spinner(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, getFonts());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSpinner.setAdapter(adapter);
        layout.addView(fontSpinner);

        // Text size
        LinearLayout sizeRow = new LinearLayout(getContext());
        sizeRow.setOrientation(HORIZONTAL);
        sizeRow.setPadding(0, 4, 0, 4);

        TextView sizeLabel = new TextView(getContext());
        sizeLabel.setText(R.string.text_size);
        sizeLabel.setTextColor(Color.WHITE);
        sizeLabel.setTextSize(12);
        sizeLabel.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));

        textSizeBar = new SeekBar(getContext());
        textSizeBar.setMax(100);
        textSizeBar.setProgress(40);
        textSizeBar.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 2));

        sizeRow.addView(sizeLabel);
        sizeRow.addView(textSizeBar);

        layout.addView(sizeRow);

        return layout;
    }

    private EditText createCompactNumberInput(String hint) {
        EditText editText = new EditText(getContext());
        editText.setHint(hint);
        editText.setHintTextColor(Color.GRAY);
        editText.setTextColor(Color.WHITE);
        editText.setTextSize(12);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        editText.setPadding(8, 6, 8, 6);
        editText.setBackgroundColor(Color.parseColor("#34495E"));
        editText.setGravity(Gravity.CENTER);
        return editText;
    }

    private View createHorizontalSpacer(int widthDp) {
        View spacer = new View(getContext());
        spacer.setLayoutParams(new LayoutParams(dpToPx(widthDp), LayoutParams.MATCH_PARENT));
        return spacer;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private String[] getFonts() {
        return new String[]{"Arial", "Roboto", "Times", "Courier"};
    }

    public void setElement(DesignElement element) {
        this.currentElement = element;
        updateProperties();
    }

    private void updateProperties() {
        if (currentElement == null) return;

        xInput.setText(String.valueOf((int)currentElement.getX()));
        yInput.setText(String.valueOf((int)currentElement.getY()));

        if (currentElement instanceof RectangleElement) {
            RectangleElement rect = (RectangleElement) currentElement;
            widthInput.setText(String.valueOf((int)rect.getWidth()));
            heightInput.setText(String.valueOf((int)rect.getHeight()));
            colorPreview.setBackgroundColor(rect.getColor());

            // Скрываем текстовую секцию
            if (textSection != null) {
                textSection.setVisibility(GONE);
            }

        } else if (currentElement instanceof TextElement) {
            TextElement text = (TextElement) currentElement;
            textInput.setText(text.getText());

            // Показываем текстовую секцию
            if (textSection != null) {
                textSection.setVisibility(VISIBLE);
            }
        }
    }

    private void openColorPicker() {
        ColorPickerDialog dialog = new ColorPickerDialog(getContext(), color -> {
            colorPreview.setBackgroundColor(color);
            if (listener != null && currentElement != null) {
                listener.onColorChanged(currentElement, color);
            }
        });
        dialog.show();
    }

    private void openBorderColorPicker() {
        // Аналогично
    }

    public interface OnPropertyChangedListener {
        void onColorChanged(DesignElement element, int newColor);
        void onPositionChanged(DesignElement element, float x, float y);
        void onSizeChanged(DesignElement element, float width, float height);
        void onRotationChanged(DesignElement element, float rotation);
        void onOpacityChanged(DesignElement element, int opacity);
        void onTextChanged(TextElement element, String newText);
    }

    public void setOnPropertyChangedListener(OnPropertyChangedListener listener) {
        this.listener = listener;
    }
}