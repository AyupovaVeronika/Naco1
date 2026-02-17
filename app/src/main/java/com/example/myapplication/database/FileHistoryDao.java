package com.example.myapplication.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface FileHistoryDao {

    @Insert
    void insert(FileHistory fileHistory);

    @Update
    void update(FileHistory fileHistory);

    @Delete
    void delete(FileHistory fileHistory);

    @Query("SELECT * FROM file_history ORDER BY lastOpened DESC")
    List<FileHistory> getAllHistory();

    @Query("SELECT * FROM file_history WHERE filePath = :filePath")
    FileHistory getByFilePath(String filePath);

    @Query("DELETE FROM file_history")
    void deleteAll();
}