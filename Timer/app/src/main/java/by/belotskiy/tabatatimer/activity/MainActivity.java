package by.belotskiy.tabatatimer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import by.belotskiy.tabatatimer.adapter.CustomAdapter;
import by.belotskiy.tabatatimer.R;
import by.belotskiy.tabatatimer.database.WorkoutDatabase;
import by.belotskiy.tabatatimer.model.WorkoutModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static WorkoutDatabase workoutDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.trainings);
        textView.setTextSize(SplashScreenActivity.font_size);

        workoutDatabase = Room.databaseBuilder(getApplicationContext(), WorkoutDatabase.class, "workoutdb").allowMainThreadQueries().build();

        final List<WorkoutModel> trainings = workoutDatabase.workoutDao().getAll();
        for (int i = 0; i < trainings.size(); i++) {
            WorkoutModel workoutModel = trainings.get(i);
            workoutModel.updateDescription();
            trainings.set(i, workoutModel);
        }
        final ListView listView = (ListView) findViewById(R.id.lvMain);


        listView.setAdapter(new CustomAdapter(trainings, this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(view.getContext(), "Opening " + trainings.get(i).getWorkoutName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), SequenceActivity.class);
                intent.putExtra("SEQUENCE", trainings.get(i));
                startActivity(intent);
            }
        });
    }

    public void floatingButtonClick(View view) {
        Intent intent = new Intent(this, SequenceActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        menu.add(R.string.settings);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }

}