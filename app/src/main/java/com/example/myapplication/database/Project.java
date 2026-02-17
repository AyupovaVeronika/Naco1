package com.example.myapplication.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.myapplication.redactor.DesignElement;

import java.util.List;

@Entity(tableName = "projects")
public class Project {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private long lastModified;
    private String thumbnailPath;

    @TypeConverters(Converters.class)
    private List<DesignElement> elements;
}