package by.belotskiy.tabatatimer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import by.belotskiy.tabatatimer.R;
import by.belotskiy.tabatatimer.adapter.SequenceAdapter;
import by.belotskiy.tabatatimer.model.SequenceControllerModel;
import by.belotskiy.tabatatimer.model.WorkoutModel;

import java.util.ArrayList;
import java.util.Arrays;

public class SequenceActivity extends AppCompatActivity{

    WorkoutModel workout;
    ArrayList<SequenceControllerModel> items;
    TextView text;
    public static boolean isCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_sequence);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        workout = (WorkoutModel) getIntent().getSerializableExtra("SEQUENCE");
        ListView listView = (ListView) findViewById(R.id.sequenceListView);
        text = (TextView) findViewById(R.id.training_name);

        if (savedInstanceState != null) {
            items = (ArrayList<SequenceControllerModel>) savedInstanceState.getSerializable("value");
        }
        else {
            items = new ArrayList<SequenceControllerModel>();
            ArrayList<Integer> baseValues;
            if (workout == null) {
                workout = new WorkoutModel();
                baseValues = workout.getBasicValues();
                workout.setBasicValues(MainActivity.workoutDatabase.workoutDao().getCount());
                isCreated = false;
            } else {
                baseValues = new ArrayList<Integer>(Arrays.asList(workout.getPreparationTime(), workout.getStretchTime(),
                        workout.getWorkTime(), workout.getRelaxTime(), workout.getCyclesNumber(),
                        workout.getSetsNumber(), workout.getRelaxAfterSetTime()));
                text.setText(workout.getWorkoutName());
                isCreated = true;
            }
            items.add(new SequenceControllerModel(R.drawable.directions_walk, getString(R.string.preparation), baseValues.get(0)));
            items.add(new SequenceControllerModel(R.drawable.directions_run, getString(R.string.stretch), baseValues.get(1)));
            items.add(new SequenceControllerModel(R.drawable.access_alarm, getString(R.string.work), baseValues.get(2)));
            items.add(new SequenceControllerModel(R.drawable.accessibility, getString(R.string.rest), baseValues.get(3)));
            items.add(new SequenceControllerModel(R.drawable.repeat, getString(R.string.cycle), baseValues.get(4)));
            items.add(new SequenceControllerModel(R.drawable.update, getString(R.string.sets), baseValues.get(5)));
            items.add(new SequenceControllerModel(R.drawable.weekend, getString(R.string.sets_rest), baseValues.get(6)));

        }
        listView.setAdapter(new SequenceAdapter(items, workout, this, ""));
    }

    @Override
    protected void onResume() {
        super.onResume();
        text = (TextView) findViewById(R.id.training_name);
        text.setTextSize(SplashScreenActivity.font_size);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("value", items);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        items = (ArrayList<SequenceControllerModel>) savedInstanceState.getSerializable("value");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        workout = (WorkoutModel) data.getSerializableExtra("SEQUENCE");
    }

    public void toListOfSequences(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void toEditSequencePage(View view) {
        Intent intent = new Intent(this, EditSequenceActivity.class);
        workout.setPreparationTime(items.get(0).getBaseValue());
        workout.setStretchTime(items.get(1).getBaseValue());
        workout.setWorkTime(items.get(2).getBaseValue());
        workout.setRelaxTime(items.get(3).getBaseValue());
        workout.setCyclesNumber(items.get(4).getBaseValue());
        workout.setSetsNumber(items.get(5).getBaseValue());
        workout.setRelaxAfterSetTime(items.get(6).getBaseValue());
        workout.updateWorkingOrder();

        intent.putExtra("SEQUENCE", workout);
        startActivityForResult(intent, 1);
    }

    public void startTimer(View view) {
        Intent intent = new Intent(this, TimerActivity.class);
        intent.putExtra("SEQUENCE", workout);
        startActivity(intent);
    }
}
