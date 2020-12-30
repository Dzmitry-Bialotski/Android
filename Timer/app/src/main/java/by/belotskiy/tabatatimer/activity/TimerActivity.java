package by.belotskiy.tabatatimer.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import by.belotskiy.tabatatimer.R;
import by.belotskiy.tabatatimer.view_model.SavedStateViewModel;
import by.belotskiy.tabatatimer.timer.TimerHandler;
import by.belotskiy.tabatatimer.timer.TimerService;
import by.belotskiy.tabatatimer.model.WorkoutModel;

import java.util.ArrayList;

public class TimerActivity extends AppCompatActivity {
    TimerHandler handler;
    WorkoutModel workout;

    private static TextView timer;
    public boolean lockedButtons = false;
    private TextView cycleNumber;
    private ListView listView;
    private ImageButton playPause;
    private ImageButton lock;
    private ImageButton nextStage;
    private ImageButton prevStage;
    public static MediaPlayer sound;
    SavedStateViewModel model;

    final int TASK1_CODE = 1;

    public final static int STATUS_FINISH = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        model = ViewModelProviders.of(this).get(SavedStateViewModel.class);
        workout = (WorkoutModel) getIntent().getSerializableExtra("SEQUENCE");
        getWindow().getDecorView().setBackgroundColor(workout.getColor());
        timer = (TextView) findViewById(R.id.timer);
        cycleNumber = (TextView) findViewById(R.id.stage);
        listView = (ListView) findViewById(R.id.stages);
        lock = (ImageButton) findViewById(R.id.lockUnlock);
        playPause = (ImageButton) findViewById(R.id.playPause);
        nextStage = (ImageButton) findViewById(R.id.nextStage);
        prevStage = (ImageButton) findViewById(R.id.prevStage);
        TextView holeTime = (TextView) findViewById(R.id.hole_time);

        sound = MediaPlayer.create(this, R.raw.censor_beep);
        int cycle_value = workout.getCyclesNumber() * workout.getSetsNumber();
        int time = 0;
        for (Integer seconds : workout.getWorkoutOrderTime()) {
            time += seconds;
        }
        int min = time / 60;
        int seconds = time - min * 60;
        holeTime.setText(String.valueOf(min) + ":" + seconds);
        listView.setBackgroundColor(workout.getColor());

        if (model.getSate() == null) {
            timer.setText(workout.getWorkoutOrderTime().get(0).toString());
            cycleNumber.setText(String.valueOf(0) + "/" + cycle_value);

            ArrayList<String> array = new ArrayList<String>();
            int stages_number = workout.getWorkoutOrder().size();
            for (int i = 0; i < stages_number; i++) {
                array.add(String.valueOf(i + 1) + ". " + workout.getWorkoutOrder().get(i));
            }

            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array);
            listView.setAdapter(arrayAdapter);

            PendingIntent pi = createPendingResult(TASK1_CODE, new Intent(this, TimerService.class), 0);
            handler = new TimerHandler(this, workout, pi, array);
            model.saveState(handler);
        }
        else {
            handler = model.getSate();
            setText(TimerHandler.timeLeftInMilliseconds);
            cycleNumber.setText(String.valueOf(handler.currentCycle) + "/" + cycle_value);
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, handler.array);
            listView.setAdapter(arrayAdapter);
            handler.startTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.stopTimer();
        model.saveState(handler);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == STATUS_FINISH) {
            int result = data.getIntExtra("RESULT", 0);
            if (result == 100)
                restartTimer();
            else if (result == 0) {
                handler.stopTimer();
            }
        }
        model.saveState(handler);
    }

    public void lockUnlock(View view) {
        if (lockedButtons) {
            lock.setImageResource(R.drawable.ic_baseline_lock_open_24);
            enableButtons();
            lockedButtons = false;
        }
        else {
            lock.setImageResource(R.drawable.ic_baseline_lock_24);
            disableButtons();
            lockedButtons = true;
        }
    }

    public void restartTimer() {
        handler.stopTimer();
        handler.stage++;
        if (handler.stage != handler.workout.getWorkoutOrderTime().size()) {
            handler.array.remove(0);
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, handler.array);
            listView.setAdapter(arrayAdapter);
            if (handler.workout.getWorkoutOrder().get(handler.stage).equals("Work")) {
                updateStageTextView();
            }
            TimerHandler.timeLeftInMilliseconds = (handler.workout.getWorkoutOrderTime().get(handler.stage) + 1) * 1000;
            handler.startTimer();
        } else {
            handler.stopTimer();
            lock.setClickable(false);
            lock.setEnabled(false);
            disableButtons();
        }
        model.saveState(handler);
    }

    public static void playSound() {
        sound.start();
    }

    public static void setText(long time) {
        TimerHandler.timeLeftInMilliseconds = time;
        timer.setText(String.valueOf((TimerHandler.timeLeftInMilliseconds / 1000) - 1));
    }

    public void playPause(View view) {
        startStop();
    }

    public void startStop() {
        if (handler.timerRunning) {
            handler.stopTimer();
            playPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            handler.timerRunning = false;
        }
        else {
            handler.startTimer();
            playPause.setImageResource(R.drawable.pause);
            handler.timerRunning = true;
        }
        model.saveState(handler);
    }

    public void updateStageTextView() {
        int cycle_value = handler.workout.getCyclesNumber() * handler.workout.getSetsNumber();
        handler.currentCycle++;
        if (handler.currentCycle < cycle_value + 1) {
            cycleNumber.setText(String.valueOf(handler.currentCycle) + "/" + cycle_value);
        }
        model.saveState(handler);
    }

    private void enableButtons() {
        playPause.setClickable(true);
        playPause.setEnabled(true);
        prevStage.setClickable(true);
        prevStage.setEnabled(true);
        nextStage.setClickable(true);
        nextStage.setEnabled(true);
    }

    private void disableButtons() {
        playPause.setClickable(false);
        playPause.setEnabled(false);
        prevStage.setClickable(false);
        prevStage.setEnabled(false);
        nextStage.setClickable(false);
        nextStage.setEnabled(false);
    }

    public void prevStage(View view) {
        if (handler.stage != 0) {
            handler.stage--;
            handler.stopTimer();
            handler.array = new ArrayList<String>();
            int stages_number = handler.workout.getWorkoutOrder().size();
            for (int i = handler.stage; i < stages_number; i++)
            {
                handler.array.add(String.valueOf(i + 1) + ". " + handler.workout.getWorkoutOrder().get(i));
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, handler.array);
            listView.setAdapter(arrayAdapter);

            if (handler.workout.getWorkoutOrder().get(handler.stage + 1).equals("Work")) {
                int cycle_value = handler.workout.getCyclesNumber() * handler.workout.getSetsNumber();
                handler.currentCycle--;
                if (handler.currentCycle < cycle_value + 1) {
                    cycleNumber.setText(String.valueOf(handler.currentCycle) + "/" + cycle_value);
                }
            }
            handler.timeLeftInMilliseconds = (handler.workout.getWorkoutOrderTime().get(handler.stage) + 1) * 1000;
            handler.startTimer();
        }
        model.saveState(handler);
    }

    public void nextStage(View view) {
        if(handler.stage < handler.workout.getWorkoutOrder().size() - 1){
            restartTimer();
        }
        else if (handler.stage == handler.workout.getWorkoutOrder().size() - 1) {
            handler.stopTimer();
            handler.stage++;
            timer.setText("0");
            lock.setClickable(false);
            lock.setEnabled(false);
            disableButtons();
        }
        model.saveState(handler);
    }
}
