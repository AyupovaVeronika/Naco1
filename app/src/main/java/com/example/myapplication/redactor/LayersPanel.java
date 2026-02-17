package com.example.myapplication.redactor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.EditActivity;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LayersPanel extends LinearLayout {
    private RecyclerView layersRecyclerView;
    private LayersAdapter adapter;
    private List<DesignElement> elements;
    private OnLayerActionListener listener;

    // Состояние сворачивания
    private boolean isExpanded = true;
    private LinearLayout contentContainer;

    public LayersPanel(Context context, AttributeSet attrs) {
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
        title.setText(R.string.layers);
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

        // Контейнер для контента
        contentContainer = new LinearLayout(context);
        contentContainer.setOrientation(VERTICAL);
        contentContainer.setPadding(0, 8, 0, 0);

        // Кнопки управления слоями
        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setOrientation(HORIZONTAL);
        buttonLayout.setPadding(8, 8, 8, 8);

        Button btnGroup = createCompactButton("Груп.");
        Button btnUngroup = createCompactButton("Разгр.");
        Button btnDuplicate = createCompactButton("Дубл.");
        Button btnDelete = createCompactButton("Удал.");

        buttonLayout.addView(btnGroup);
        buttonLayout.addView(btnUngroup);
        buttonLayout.addView(btnDuplicate);
        buttonLayout.addView(btnDelete);

        contentContainer.addView(buttonLayout);

        // RecyclerView для списка слоев
        layersRecyclerView = new RecyclerView(context);
        layersRecyclerView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        ));

        adapter = new LayersAdapter();
        layersRecyclerView.setAdapter(adapter);
        layersRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Добавляем поддержку перетаскивания для изменения порядка слоев
        setupDragAndDrop();

        contentContainer.addView(layersRecyclerView);
        addView(contentContainer);

        // Устанавливаем слушатели
        setupListeners(btnGroup, btnUngroup, btnDuplicate, btnDelete);
    }

    private Button createCompactButton(String text) {
        Button button = new Button(getContext());
        button.setText(text);
        button.setTextSize(12);
        button.setLayoutParams(new LayoutParams(
                0,
                LayoutParams.WRAP_CONTENT,
                1
        ));
        button.setPadding(4, 8, 4, 8);
        button.setBackgroundColor(Color.parseColor("#3498DB"));
        button.setTextColor(Color.WHITE);
        return button;
    }

    private void setupDragAndDrop() {
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                if (listener != null) {
                    DesignElement element = elements.get(fromPosition);
                    listener.onLayerMoved(element, toPosition);
                }

                // Обновляем порядок в списке
                Collections.swap(elements, fromPosition, toPosition);
                adapter.notifyItemMoved(fromPosition, toPosition);

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Не используем свайп
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(layersRecyclerView);
    }

    private void setupListeners(Button group, Button ungroup, Button duplicate, Button delete) {
        group.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGroup();
                Toast.makeText(getContext(), "Группировка", Toast.LENGTH_SHORT).show();
            }
        });

        ungroup.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUngroup();
                Toast.makeText(getContext(), "Разгруппировка", Toast.LENGTH_SHORT).show();
            }
        });

        duplicate.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDuplicate();
                Toast.makeText(getContext(), "Дублирование", Toast.LENGTH_SHORT).show();
            }
        });

        delete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete();
                Toast.makeText(getContext(), "Удаление", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateLayers(List<DesignElement> elements) {
        this.elements = new ArrayList<>(elements);
        adapter.notifyDataSetChanged();
    }

    public void setOnLayerActionListener(OnLayerActionListener listener) {
        this.listener = listener;
    }

    public interface OnLayerActionListener {
        void onLayerSelected(DesignElement element);
        void onLayerVisibilityToggled(DesignElement element);
        void onLayerLockedToggled(DesignElement element);
        void onGroup();
        void onUngroup();
        void onDuplicate();
        void onDelete();
        void onLayerMoved(DesignElement element, int newPosition);
    }

    // Адаптер для RecyclerView
    private class LayersAdapter extends RecyclerView.Adapter<LayersAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_layer, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            DesignElement element = elements.get(position);
            holder.bind(element, position);
        }

        @Override
        public int getItemCount() {
            return elements != null ? elements.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView icon;
            TextView name;
            ImageView visibility;
            ImageView lock;
            ImageView dragHandle;

            ViewHolder(View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.layer_icon);
                name = itemView.findViewById(R.id.layer_name);
                visibility = itemView.findViewById(R.id.layer_visibility);
                lock = itemView.findViewById(R.id.layer_lock);
                dragHandle = itemView.findViewById(R.id.drag_handle);
            }

            void bind(DesignElement element, int position) {
                // Устанавливаем иконку в зависимости от типа элемента
                if (element instanceof RectangleElement) {
                    icon.setImageResource(android.R.drawable.ic_menu_crop);
                } else if (element instanceof TextElement) {
                    icon.setImageResource(android.R.drawable.ic_menu_edit);
                } else if (element instanceof CircleElement) {
                    icon.setImageResource(android.R.drawable.ic_menu_gallery);
                }

                // Имя слоя
                String typeName = "Слой " + (position + 1);
                if (element instanceof RectangleElement) {
                    typeName = "Прямоугольник";
                } else if (element instanceof TextElement) {
                    typeName = "Текст";
                } else if (element instanceof CircleElement) {
                    typeName = "Круг";
                }
                name.setText(typeName);

                // Иконка видимости
                if (element.isVisible()) {
                    visibility.setImageResource(android.R.drawable.ic_menu_view);
                    visibility.setColorFilter(Color.WHITE);
                } else {
                    visibility.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                    visibility.setColorFilter(Color.GRAY);
                }

                // Иконка блокировки
                if (element.isLocked()) {
                    lock.setImageResource(android.R.drawable.ic_lock_lock);
                    lock.setColorFilter(Color.RED);
                } else {
                    lock.setImageResource(R.drawable.ic_lock_open);
                    lock.setColorFilter(Color.WHITE);
                }

                // Обработчики кликов
                itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onLayerSelected(element);
                });

                // Обработчик для видимости
                visibility.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onLayerVisibilityToggled(element);
                        // Обновляем иконку
                        if (element.isVisible()) {
                            visibility.setImageResource(android.R.drawable.ic_menu_view);
                            visibility.setColorFilter(Color.WHITE);
                        } else {
                            visibility.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                            visibility.setColorFilter(Color.GRAY);
                        }
                    }
                });

                // Обработчик для блокировки
                lock.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onLayerLockedToggled(element);
                        // Обновляем иконку
                        if (element.isLocked()) {
                            lock.setImageResource(android.R.drawable.ic_lock_lock);
                            lock.setColorFilter(Color.RED);
                        } else {
                            lock.setImageResource(R.drawable.ic_lock_open);
                            lock.setColorFilter(Color.WHITE);
                        }
                    }
                });
                itemView.setOnLongClickListener(v -> {
                    if (listener != null) {
                        // Создаем и показываем контекстное меню
                        // Для этого нужно передать контекст в адаптер
                        Context context = itemView.getContext();
                        if (context instanceof EditActivity) {
                            ((EditActivity) context).showLayerContextMenu(element);
                        }
                    }
                    return true;
                });


                // Если есть drag handle, делаем его перетаскиваемым
                if (dragHandle != null) {
                    dragHandle.setOnTouchListener((v, event) -> {
                        // Здесь можно добавить начало перетаскивания
                        return false;
                    });
                }
            }
        }
    }
}