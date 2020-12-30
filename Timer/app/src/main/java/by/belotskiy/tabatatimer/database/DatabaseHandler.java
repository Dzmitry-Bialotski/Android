package by.belotskiy.tabatatimer.database;

import android.app.Application;

import androidx.room.Room;

public class DatabaseHandler extends Application {

    public static DatabaseHandler instance;

    private WorkoutDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, WorkoutDatabase.class, "WorkoutDatabase")
                .build();
    }

    public static DatabaseHandler getInstance() {
        return instance;
    }

    public WorkoutDatabase getDatabase() {
        return database;
    }
}