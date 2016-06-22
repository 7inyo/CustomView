package com.qf.customview;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class AnimationUtils {

    public static RotateAnimation initRotateAnimation(boolean isClockWise, long duration, boolean isFillAfter,
            int repeatCount) {
        int endAngle;
        if (isClockWise) {
            endAngle = 360;
        } else {
            endAngle = -360;
        }
        RotateAnimation mAni = new RotateAnimation(0, endAngle, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lirInterpolator = new LinearInterpolator();
        mAni.setInterpolator(lirInterpolator);
        mAni.setDuration(duration);
        mAni.setFillAfter(isFillAfter);
        mAni.setRepeatCount(repeatCount);
        mAni.setRepeatMode(Animation.RESTART);
        return mAni;
    }
}
