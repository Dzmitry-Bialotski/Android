package by.belotskiy.tabatatimer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import by.belotskiy.tabatatimer.R;
import by.belotskiy.tabatatimer.adapter.SequenceAdapter;
import by.belotskiy.tabatatimer.model.SequenceControllerModel;
import by.belotskiy.tabatatimer.model.WorkoutModel;

import java.util.ArrayList;
import java.util.Objects;

public class EditSequenceActivity extends AppCompatActivity {
    ArrayList<SequenceControllerModel> items_order;
    private WorkoutModel workout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sequence);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        workout = (WorkoutModel) getIntent().getSerializableExtra("SEQUENCE");

        ListView listView = (ListView) findViewById(R.id.editSequenceListView);
        EditText editText = (EditText) findViewById(R.id.editPageTextView);
        editText.setText(workout.getWorkoutName());
        editText.setTextSize(SplashScreenActivity.font_size);
        TextView textView = (TextView) findViewById(R.id.training_name);
        textView.setTextSize(SplashScreenActivity.font_size);

        if (savedInstanceState != null) {
            items_order = (ArrayList<SequenceControllerModel>) savedInstanceState.getSerializable("value");
        }
        else {
            items_order = new ArrayList<SequenceControllerModel>();

            ArrayList<String> fazes = workout.getWorkoutOrder();
            ArrayList<Integer> fazes_time = workout.getWorkoutOrderTime();
            for(int i = 0; i < fazes.size(); i++) {
                int imageId = 0;
                String faze = fazes.get(i);
                switch (faze) {
                    case "Work": imageId = R.drawable.access_alarm; break;
                    case "Rest": imageId = R.drawable.accessibility; break;
                    case "Preparation": imageId = R.drawable.directions_walk; break;
                    case "Stretch": imageId = R.drawable.directions_run; break;
                    case "SetsRest": imageId = R.drawable.weekend; break;
                    default: break;
                }
                items_order.add(new SequenceControllerModel(imageId, faze, fazes_time.get(i)));
            }
        }

        listView.setAdapter(new SequenceAdapter(items_order, workout, this, ""));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("value", items_order);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        items_order = (ArrayList<SequenceControllerModel>) savedInstanceState.getSerializable("value");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SequenceActivity.class);
        intent.putExtra("SEQUENCE", workout);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    public void saveChangesButton(View view) {
        ArrayList<String> fazes = workout.getWorkoutOrder();
        ArrayList<Integer> fazes_time = workout.getWorkoutOrderTime();
        EditText editText = (EditText)findViewById(R.id.editPageTextView);
        workout.setWorkoutName(editText.getText().toString());
        workout.updateDescription();

        for (int i = 0; i < fazes.size(); i++) {
            fazes.set(i, items_order.get(i).getTitle());
            fazes_time.set(i, items_order.get(i).getBaseValue());
        }

        if (SequenceActivity.isCreated)
            workout.updateWorkout(MainActivity.workoutDatabase);
        else {
            WorkoutModel row = MainActivity.workoutDatabase.workoutDao().getByName(workout.getWorkoutName());
            if (row == null) {
                workout.saveWorkout(MainActivity.workoutDatabase);
                Toast.makeText(this, "Sequence created successfully", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Sequence with such name exist", Toast.LENGTH_LONG).show();
            }
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}