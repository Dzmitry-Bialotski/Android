package by.belotskiy.tabatatimer.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import by.belotskiy.tabatatimer.model.WorkoutModel;

@Dao
public interface WorkoutDao {
    @Query("SELECT * FROM workouts")
    List<WorkoutModel> getAll();

    @Query("SELECT * FROM workouts WHERE id = :id")
    WorkoutModel getById(long id);

    @Query("SELECT * FROM workouts WHERE workout_name = :name")
    WorkoutModel getByName(String name);

    @Query("SELECT * FROM workouts WHERE id IN (:userIds)")
    List<WorkoutModel> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM workouts WHERE workout_name LIKE :name")
    WorkoutModel findByName(String name);

    @Query("SELECT COUNT(id) FROM workouts")
    int getCount();

    @Insert
    void insert(WorkoutModel workout);

    @Update
    void update(WorkoutModel workout);

    @Delete
    void delete(WorkoutModel workout);

    @Query("DELETE FROM workouts")
    void dropTable();
}
