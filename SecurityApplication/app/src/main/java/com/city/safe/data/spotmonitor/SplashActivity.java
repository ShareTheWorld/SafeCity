package com.city.safe.data.spotmonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.city.safe.R;

public class SplashActivity extends Activity {
	private static final int SHOW_TIME_MIN = 3000;// 最小显示时间
	private long mStartTime;// 开始时间

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			mHandler.postDelayed(goToMainActivity, SHOW_TIME_MIN
			);

		}
	};
	//进入下一个Activity
	Runnable goToMainActivity = new Runnable() {

		@Override
		public void run() {
			SplashActivity.this.startActivity(new Intent(SplashActivity.this,
					SettingActivity.class));
			finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		mHandler.sendEmptyMessage(0);
	}
}
