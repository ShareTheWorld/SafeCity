package com.city.safe.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.city.safe.R;
import com.city.safe.psemission.PermissionHelper;

public class BootActivity extends Activity {
	private PermissionHelper mPermissionHelper;
	private static final String TAG="BootActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (ActivityCompat.checkSelfPermission(this,
				Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.SYSTEM_ALERT_WINDOW }, 5);
		}

		mPermissionHelper = new PermissionHelper(this);
		mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
			@Override
			public void onAfterApplyAllPermission() {
				Log.i(TAG, "All of requested permissions has been granted, so run app logic.");
				init();
			}
		});
		if (Build.VERSION.SDK_INT < 23) {
			// 如果系统版本低于23，直接跑应用的逻辑
			Log.d(TAG, "The api level of system is lower than 23, so run app logic directly.");
			init();
		} else {
			// 如果权限全部申请了，那就直接跑应用逻辑
			if (mPermissionHelper.isAllRequestedPermissionGranted()) {
				Log.d(TAG, "All of requested permissions has been granted, so run app logic directly.");
				init();
			} else {
				// 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
				Log.i(TAG, "Some of requested permissions hasn't been granted, so apply permissions first.");
				mPermissionHelper.applyPermissions();
			}
		}
	}



	@SuppressLint("Override")
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mPermissionHelper.onActivityResult(requestCode, resultCode, data);
	}

	public void init() {
			Intent intent = new Intent(this, LoginActivity.class);
			intent.setPackage("com.city.safe");
			this.startActivity(intent);
			this.finish();
	}
}
