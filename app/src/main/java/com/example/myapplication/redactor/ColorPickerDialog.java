package com.example.myapplication.redactor;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.R;

public class ColorPickerDialog extends Dialog {
    private OnColorSelectedListener listener;
    private View colorPreview;
    private SeekBar redSeekBar, greenSeekBar, blueSeekBar;
    private TextView redValue, greenValue, blueValue, hexValue;
    private int currentColor = Color.BLUE;

    public ColorPickerDialog(@NonNull Context context, OnColorSelectedListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createDialogLayout());
        initViews();
        setupListeners();
    }

    private LinearLayout createDialogLayout() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        layout.setBackgroundColor(Color.WHITE);

        // Заголовок
        TextView title = new TextView(getContext());
        title.setText(R.string.select_color);
        title.setTextSize(20);
        title.setPadding(0, 0, 0, 30);
        layout.addView(title);

        // Превью цвета
        colorPreview = new View(getContext());
        colorPreview.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 200));
        colorPreview.setBackgroundColor(currentColor);
        layout.addView(colorPreview);

        // Красный
        layout.addView(createColorControl(getContext().getString(R.string.red), 0));
        // Зеленый
        layout.addView(createColorControl(getContext().getString(R.string.green), 1));
        // Синий
        layout.addView(createColorControl(getContext().getString(R.string.blue), 2));

        // HEX значение
        LinearLayout hexLayout = new LinearLayout(getContext());
        hexLayout.setOrientation(LinearLayout.HORIZONTAL);
        hexLayout.setPadding(0, 20, 0, 20);

        TextView hexLabel = new TextView(getContext());
        hexLabel.setText(R.string.hex);
        hexLabel.setTextSize(16);

        hexValue = new TextView(getContext());
        hexValue.setText("#0000FF");
        hexValue.setTextSize(16);
        hexValue.setTypeface(hexValue.getTypeface(), android.graphics.Typeface.BOLD);

        hexLayout.addView(hexLabel);
        hexLayout.addView(hexValue);
        layout.addView(hexLayout);

        // Кнопки
        LinearLayout buttonLayout = new LinearLayout(getContext());
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, 20, 0, 0);

        Button btnCancel = new Button(getContext());
        btnCancel.setText(R.string.cancel);
        btnCancel.setOnClickListener(v -> dismiss());

        Button btnOk = new Button(getContext());
        btnOk.setText(R.string.ok);
        btnOk.setOnClickListener(v -> {
            if (listener != null) {
                listener.onColorSelected(currentColor);
            }
            dismiss();
        });

        buttonLayout.addView(btnCancel);
        buttonLayout.addView(btnOk);
        layout.addView(buttonLayout);

        return layout;
    }

    private LinearLayout createColorControl(String name, int colorIndex) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(0, 10, 0, 10);

        TextView label = new TextView(getContext());
        label.setText(name);
        layout.addView(label);

        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);

        SeekBar seekBar = new SeekBar(getContext());
        seekBar.setMax(255);
        seekBar.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView value = new TextView(getContext());
        value.setWidth(80);
        value.setGravity(android.view.Gravity.CENTER);

        switch (colorIndex) {
            case 0:
                redSeekBar = seekBar;
                redValue = value;
                seekBar.setProgress(Color.red(currentColor));
                value.setText(String.valueOf(Color.red(currentColor)));
                break;
            case 1:
                greenSeekBar = seekBar;
                greenValue = value;
                seekBar.setProgress(Color.green(currentColor));
                value.setText(String.valueOf(Color.green(currentColor)));
                break;
            case 2:
                blueSeekBar = seekBar;
                blueValue = value;
                seekBar.setProgress(Color.blue(currentColor));
                value.setText(String.valueOf(Color.blue(currentColor)));
                break;
        }

        row.addView(seekBar);
        row.addView(value);
        layout.addView(row);

        return layout;
    }

    private void initViews() {
        updateColor();
    }

    private void setupListeners() {
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        redSeekBar.setOnSeekBarChangeListener(listener);
        greenSeekBar.setOnSeekBarChangeListener(listener);
        blueSeekBar.setOnSeekBarChangeListener(listener);
    }

    private void updateColor() {
        int red = redSeekBar.getProgress();
        int green = greenSeekBar.getProgress();
        int blue = blueSeekBar.getProgress();

        currentColor = Color.rgb(red, green, blue);
        colorPreview.setBackgroundColor(currentColor);

        redValue.setText(String.valueOf(red));
        greenValue.setText(String.valueOf(green));
        blueValue.setText(String.valueOf(blue));

        hexValue.setText(String.format("#%02X%02X%02X", red, green, blue));
    }

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }
}