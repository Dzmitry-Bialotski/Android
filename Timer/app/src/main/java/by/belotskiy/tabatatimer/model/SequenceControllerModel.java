package by.belotskiy.tabatatimer.model;

import java.io.Serializable;

public class SequenceControllerModel implements Serializable {
    private int activityImage;
    private String title;
    private Integer baseValue;

    public SequenceControllerModel(int activityImage, String title, Integer baseValue) {
        this.activityImage = activityImage;
        this.title = title;
        this.baseValue = baseValue;
    }

    public int getActivityImage() {
        return activityImage;
    }

    public void setActivityImage(int activityImage) {
        this.activityImage = activityImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(Integer baseValue) {
        this.baseValue = baseValue;
    }
}
