package com.qf.customview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class LeafActivity extends Activity {
    
    private static String TAG = "qiufei";
    
    private ImageView fan;
    private RotateAnimation mAni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "LeafActivity on create");
        setContentView(R.layout.leaf);
        initViews();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "leaf activity on resume");
        
    }
    
    private void initViews() {
        Log.d(TAG, "init views");
//        ViewTreeObserver vto = llv.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                llv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                w=llv.getWidth();
//                h=llv.getHeight();
//                Log.d(TAG, "w="+w+", h="+h);
//            }
//        });
        fan = (ImageView) findViewById(R.id.fan_pic);
        mAni = AnimationUtils.initRotateAnimation(true, 1000, true, Animation.INFINITE);
        fan.startAnimation(mAni);
    }
}
