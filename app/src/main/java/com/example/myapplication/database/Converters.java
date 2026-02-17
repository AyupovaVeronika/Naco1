package com.example.myapplication.database;

import androidx.room.TypeConverter;

import com.example.myapplication.redactor.DesignElement;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    @TypeConverter
    public static String fromElements(List<DesignElement> elements) {
        Gson gson = new Gson();
        return gson.toJson(elements);
    }

    @TypeConverter
    public static List<DesignElement> toElements(String data) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<DesignElement>>(){}.getType();
        return gson.fromJson(data, type);
    }
}