package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.FileHistory;
import com.example.myapplication.database.HistoryAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChoiseActivity extends AppCompatActivity {

    private Button btnGallery;
    private RecyclerView historyRecyclerView;
    private FloatingActionButton fabAdd;
    private HistoryAdapter historyAdapter;
    private AppDatabase database;
    private List<FileHistory> historyList = new ArrayList<>();

    // Регистрация для выбора изображения из галереи
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        processSelectedImage(selectedImageUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choise);

        // Инициализация базы данных
        database = AppDatabase.getInstance(this);

        // Инициализация views
        btnGallery = findViewById(R.id.btn_gallery);
        historyRecyclerView = findViewById(R.id.history_recycler);
        fabAdd = findViewById(R.id.fab_add);

        // Настройка RecyclerView
        setupRecyclerView();

        // Загрузка истории
        loadHistory();

        // Обработчик кнопки галереи
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // Обработчик FAB
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditActivity(null);
            }
        });
    }

    private void setupRecyclerView() {
        historyAdapter = new HistoryAdapter(historyList, new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FileHistory fileHistory) {
                // При клике на элемент истории открываем его в EditActivity
                openEditActivity(fileHistory.getFilePath());
            }
        });

        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(historyAdapter);
    }

    private void loadHistory() {
        // Загружаем историю из БД в фоновом потоке
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<FileHistory> loadedHistory = database.fileHistoryDao().getAllHistory();
                historyList.clear();
                historyList.addAll(loadedHistory);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        historyAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void processSelectedImage(Uri imageUri) {
        try {
            // Получаем путь к файлу
            String filePath = FileUtils.getPath(this, imageUri); // Нужно создать FileUtils
            String fileName = new File(filePath).getName();

            // Сохраняем в историю
            saveToHistory(filePath, fileName);

            // Открываем EditActivity с этим изображением
            openEditActivity(filePath);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при открытии изображения", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToHistory(String filePath, String fileName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Проверяем, есть ли уже такой файл в истории
                FileHistory existing = database.fileHistoryDao().getByFilePath(filePath);

                if (existing != null) {
                    // Обновляем время последнего открытия
                    existing.setLastOpened(System.currentTimeMillis());
                    database.fileHistoryDao().update(existing);
                } else {
                    // Создаем новую запись
                    FileHistory history = new FileHistory(filePath, fileName, System.currentTimeMillis());
                    database.fileHistoryDao().insert(history);
                }

                // Обновляем список в UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadHistory();
                    }
                });
            }
        }).start();
    }

    private void openEditActivity(String filePath) {
        Intent intent = new Intent(ChoiseActivity.this, EditActivity.class);
        if (filePath != null) {
            intent.putExtra("file_path", filePath);
        }
        startActivity(intent);
    }
}