package com.qf.customview;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LeafActivity extends Activity {

    private static String TAG = "felix";

    private ImageView fan;
    private RotateAnimation mAni;
    private LeafLoadView leaf;
    private int screenWidth, fanMargin;
    public static final int REFRESH_PROGRESS = 1;
    public static final int PROGRESS_DONE = 2;
    private int mProgress = 0;

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
        screenWidth = UiUtils.getScreenWidthPixels(this);
        fanMargin = UiUtils.dipToPx(this, 6);
        leaf = (LeafLoadView) findViewById(R.id.leafload);
        leaf.setHandler(mh);
        fan = (ImageView) findViewById(R.id.fan_pic);
        mAni = AnimationUtils.initRotateAnimation(true, 1000, true, Animation.INFINITE);
        fan.startAnimation(mAni);
    }

    private Handler mh = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case LeafLoadView.EVENT_ON_SIZE_CHANGED:
                int leafWidth = leaf.getWidth();
                int marginRight = (screenWidth - leafWidth) / 2 + fanMargin;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fan.getLayoutParams();
                params.setMargins(0, 0, marginRight, 0);
                fan.setLayoutParams(params);
                fan.setVisibility(View.VISIBLE);
                Log.d(TAG, "on size changed");
                break;
            case REFRESH_PROGRESS:
                if (mProgress < 40) {
                    mProgress += 1;
                    leaf.setProgress(mProgress);
                    mh.sendEmptyMessageDelayed(REFRESH_PROGRESS, new Random().nextInt(800));
                } else {
                    mProgress += 1;
                    if(mProgress>LeafLoadView.TOTAL_PROGRESS)
                        return;
                    leaf.setProgress(mProgress);
                    mh.sendEmptyMessageDelayed(REFRESH_PROGRESS, new Random().nextInt(1200));
                }
                break;
            case PROGRESS_DONE:
                Toast.makeText(LeafActivity.this, "loading done!", Toast.LENGTH_LONG).show();
                mAni.cancel();
                break;
            }
        }
    };
}
