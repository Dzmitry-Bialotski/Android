package by.belotskiy.tabatatimer.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import by.belotskiy.tabatatimer.util.TypeConverterInteger;
import by.belotskiy.tabatatimer.util.TypeConverterStrings;
import by.belotskiy.tabatatimer.model.WorkoutModel;


@Database(entities = {WorkoutModel.class}, version = 1, exportSchema = false)
@TypeConverters({TypeConverterStrings.class, TypeConverterInteger.class})
public abstract class WorkoutDatabase extends RoomDatabase {
    public abstract WorkoutDao workoutDao();
}


