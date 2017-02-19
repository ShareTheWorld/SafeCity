package com.city.safe.data.acc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import com.city.safe.R;

public class BaseActivity extends Activity {

	protected Button btn_leftTop, btn_rightTop;
	protected TextView tv_head,tv_rightTop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	protected void initHead() {
		btn_leftTop = (Button) findViewById(R.id.btn_leftTop);
		btn_rightTop = (Button) findViewById(R.id.btn_rightTop);
		tv_head = (TextView) findViewById(R.id.tv_head);
		tv_rightTop = (TextView) findViewById(R.id.tv_head);
	}
}
