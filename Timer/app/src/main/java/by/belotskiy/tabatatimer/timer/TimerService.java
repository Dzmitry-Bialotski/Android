package by.belotskiy.tabatatimer.timer;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import by.belotskiy.tabatatimer.activity.TimerActivity;

public class TimerService extends Service {
    private final static String TAG = "TimerService";
    public static final String COUNTDOWN_TIMER = "activity_timer";

    CountDownTimer countDownTimer = null;
    private long timeLeftInMilliseconds;
    PendingIntent pi;
    private static boolean running;

    public TimerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        running = true;
        timeLeftInMilliseconds = TimerHandler.timeLeftInMilliseconds;
        pi = intent.getParcelableExtra("PENDING_INTENT");
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMilliseconds = l;
                if (timeLeftInMilliseconds / 1000 == 2)
                    TimerActivity.playSound();
                TimerActivity.setText(timeLeftInMilliseconds);
                TimerHandler.timeLeftInMilliseconds = timeLeftInMilliseconds;
            }

            @Override
            public void onFinish() {
                try {
                    Intent intent;
                    if (running) {
                        intent = new Intent().putExtra("RESULT", 100);
                    }
                    else {
                        intent = new Intent().putExtra("RESULT", 0);
                    }
                    pi.send(TimerService.this, TimerActivity.STATUS_FINISH, intent);
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        return super.onStartCommand(intent, flags, startId);
    }

    public void stopTimer() {
        running = false;
        countDownTimer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
