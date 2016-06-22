package com.qf.customview;

import android.support.annotation.NonNull;

public class PieData {

    private String name;
    private float value;
    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    private float percentage;
    
    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    private int color=0;
    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    private float angle=0;
    
    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public PieData(@NonNull String name, @NonNull float value) {
        this.name = name;
        this.value = value;
    }
}
