package by.belotskiy.tabatatimer.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import by.belotskiy.tabatatimer.R;
import by.belotskiy.tabatatimer.activity.SplashScreenActivity;
import by.belotskiy.tabatatimer.database.WorkoutDatabase;
import by.belotskiy.tabatatimer.util.Converter;
import by.belotskiy.tabatatimer.util.TypeConverterInteger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;


@Entity(tableName = "workouts")
public class WorkoutModel implements Serializable {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "workout_name")
    private String workoutName;

    @ColumnInfo(name = "preparation_time")
    private int preparationTime;

    @ColumnInfo(name = "stretch_time")
    private int stretchTime;

    @ColumnInfo(name = "work_time")
    private int workTime;

    @ColumnInfo(name = "relax_time")
    private int relaxTime;

    @ColumnInfo(name = "cycles_number")
    private int cyclesNumber;

    @ColumnInfo(name = "sets_number")
    private int setsNumber;

    @ColumnInfo(name = "relax_after_set_time")
    private int relaxAfterSetTime;

    @ColumnInfo(name = "workout_order_time")
    @TypeConverters(TypeConverterInteger.class)
    private ArrayList<Integer> workoutOrderTime;

    private String description;

    @ColumnInfo(name = "color")
    private int color;

    // If id is equal to 0, then this object doesn't saved in database
    public WorkoutModel createWorkoutModel(int id, String workout_name, int preparation_time, int stretch_time, int work_time, int relax_time, int cycles_number, int sets_number, int relax_after_set_time, String workout_order_time_value, int color) {
        this.id = id;
        this.workoutName = workout_name;
        this.preparationTime = preparation_time;
        this.stretchTime = stretch_time;
        this.workTime = work_time;
        this.relaxTime = relax_time;
        this.cyclesNumber = cycles_number;
        this.setsNumber = sets_number;
        this.relaxAfterSetTime = relax_after_set_time;
        this.color = color;

        if (workout_order_time_value.equals("")) {
            createWorkingOrder();
        }
        else {
            workoutOrderTime = new ArrayList<Integer>(Arrays.asList(Converter.convertStringToIntegerArray(workout_order_time_value)));
        }

        this.description = createDescription();
        return this;
    }

    private void createWorkingOrder() {
        workoutOrderTime = new ArrayList<Integer>();
        workoutOrderTime.add(preparationTime);
        workoutOrderTime.add(stretchTime);
        for (int i = 0; i < setsNumber; i++) {
            for (int j = 0; j < cyclesNumber; j++) {
                workoutOrderTime.add(workTime);
                workoutOrderTime.add(relaxTime);

            }
            workoutOrderTime.add(relaxAfterSetTime);
        }
    }

    public void updateWorkingOrder() {
        createWorkingOrder();
    }

    public void saveWorkout(WorkoutDatabase workoutDatabase) {
        workoutDatabase.workoutDao().insert(this);
    }

    public void deleteWorkout (WorkoutDatabase workoutDatabase) {
        workoutDatabase.workoutDao().delete(this);
    }

    public void updateWorkout(WorkoutDatabase workoutDatabase) {
        workoutDatabase.workoutDao().update(this);
    }

    private String createDescription() {
        int whole_time = 0;
        for (Integer time: workoutOrderTime) {
            whole_time += time;
        }
        if (SplashScreenActivity.language.equals("en")) {
            return "Whole training time: " + whole_time + "\nCycle number: " + cyclesNumber
                    + "\nSets number: " + setsNumber;
        }
        else if (SplashScreenActivity.language.equals("ru")) {
            return "Полное время тренировки: " + whole_time + "\nКоличество циклов: " + cyclesNumber
                    + "\nКоличество сетов: " + setsNumber;
        }
        return "";
    }

    public void updateDescription() {
        this.description = createDescription();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public int getStretchTime() {
        return stretchTime;
    }

    public void setStretchTime(int stretchTime) {
        this.stretchTime = stretchTime;
    }

    public int getWorkTime() {
        return workTime;
    }

    public void setWorkTime(int workTime) {
        this.workTime = workTime;
    }

    public int getRelaxTime() {
        return relaxTime;
    }

    public void setRelaxTime(int relaxTime) {
        this.relaxTime = relaxTime;
    }

    public int getCyclesNumber() {
        return cyclesNumber;
    }

    public void setCyclesNumber(int cyclesNumber) {
        this.cyclesNumber = cyclesNumber;
    }

    public int getSetsNumber() {
        return setsNumber;
    }

    public void setSetsNumber(int setsNumber) {
        this.setsNumber = setsNumber;
    }

    public int getRelaxAfterSetTime() {
        return relaxAfterSetTime;
    }

    public void setBasicValues(int id) {
        this.id = id;
        this.workoutName = "Base name";
        this.preparationTime = 25;
        this.stretchTime = 25;
        this.workTime = 20;
        this.relaxTime = 10;
        this.cyclesNumber = 4;
        this.setsNumber = 1;
        this.relaxAfterSetTime = 40;
        this.color = R.color.colorPrimary;
        createWorkingOrder();
    }

    public ArrayList<Integer> getBasicValues() {
        return new ArrayList<Integer>(Arrays.asList(25, 25, 20, 10, 4, 1, 40));
    }

    public void setRelaxAfterSetTime(int relaxAfterSetTime) {
        this.relaxAfterSetTime = relaxAfterSetTime;
    }

    public ArrayList<Integer> getWorkoutOrderTime() {
        return workoutOrderTime;
    }

    public void setWorkoutOrderTime(ArrayList<Integer> workoutOrderTime) {
        this.workoutOrderTime = workoutOrderTime;
    }

    public ArrayList<String> getWorkoutOrder() {
        ArrayList<String> workoutOrder = new ArrayList<String>();

        workoutOrder.add("Preparation");
        workoutOrder.add("Stretch");
        for (int i = 0; i < setsNumber; i++) {
            for (int j = 0; j < cyclesNumber; j++) {
                workoutOrder.add("Work");
                workoutOrder.add("Rest");
            }
            workoutOrder.add("Rest between sets");
        }

        return workoutOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
