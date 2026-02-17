package com.example.myapplication.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "file_history")
public class FileHistory implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String filePath;
    private String fileName;
    private long lastOpened;
    private String thumbnailPath; // для миниатюры

    public FileHistory(String filePath, String fileName, long lastOpened) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.lastOpened = lastOpened;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public long getLastOpened() { return lastOpened; }
    public void setLastOpened(long lastOpened) { this.lastOpened = lastOpened; }

    public String getThumbnailPath() { return thumbnailPath; }
    public void setThumbnailPath(String thumbnailPath) { this.thumbnailPath = thumbnailPath; }
}
