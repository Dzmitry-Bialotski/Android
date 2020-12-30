package by.belotskiy.tabatatimer.view_model;

import androidx.lifecycle.ViewModel;

import by.belotskiy.tabatatimer.timer.TimerHandler;

public class SavedStateViewModel extends ViewModel {
    TimerHandler mState;
    private int stage;


    public void saveState(TimerHandler savedStateHandle) {
        mState = savedStateHandle;
    }

    public TimerHandler getSate() {
        return mState;
    }

    public int getStage() {
        return stage;
    }
    public void setStage(int stage) {
        this.stage = stage;
    }
}

