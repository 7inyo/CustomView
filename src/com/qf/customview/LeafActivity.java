package com.qf.customview;

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

public class LeafActivity extends Activity {

	private static String TAG = "felix";

	private ImageView fan;
	private RotateAnimation mAni;
	private LeafLoadView leaf;
	private int screenWidth, fanMargin;

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
			}
		}
	};
}
