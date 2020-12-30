package by.belotskiy.tabatatimer.util;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class TypeConverterStrings {

    @TypeConverter
    public static ArrayList<String> restoreList(String listOfString) {
        return new Gson().fromJson(listOfString, new TypeToken<ArrayList<String>>() {}.getType());
    }

    @TypeConverter
    public static String saveList(ArrayList<String> listOfString) {
        return new Gson().toJson(listOfString);
    }
}

