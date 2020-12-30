package by.belotskiy.tabatatimer.util;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class TypeConverterInteger {

    @TypeConverter
    public static ArrayList<Integer> restoreList(String listOfIntegers) {
        return new Gson().fromJson(listOfIntegers, new TypeToken<ArrayList<Integer>>() {}.getType());
    }

    @TypeConverter
    public static String saveList(ArrayList<Integer> listOfIntegers) {
        return new Gson().toJson(listOfIntegers);
    }
}
