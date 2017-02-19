package com.city.safe.data.spotmonitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.city.safe.R;
import com.city.safe.activity.ProjectDataActivity;
import com.city.safe.bean.FileDataEntity;
import com.city.safe.bean.UserJson;
import com.city.safe.utils.FileUtils;
import com.city.safe.utils.SPUtil;
import com.google.gson.Gson;

import org.xutils.common.util.FileUtil;

public class SettingActivity extends Activity implements OnClickListener{
	public static final int REQUEST_DATA_SPOT_ONE=1;
	public static final int REQUEST_DATA_SPOT_TWO=2;

	public static final int FROUNT_CAM = 1;
	public static final int BACK_CAM = 0;
	public static final int ONE_SPOT = 1;
	public static final int TWO_SPOT = 2;
	private EditText et_diameter;
	private Button button_done;
	private double diameter;
	private EditText width;
	private EditText height;
	private EditText skip;
	private int choice = SettingActivity.BACK_CAM;
	private int detectCount = SettingActivity.ONE_SPOT;
	private List<String> list = new ArrayList<String>();
	private List<String> modeList = new ArrayList<String>();
	private Spinner mySpinner;
	private Spinner workMode;
	private ArrayAdapter<String> adapter;
	private ArrayAdapter<String> modeAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.settinglayout);
		et_diameter=(EditText)this.findViewById(R.id.input_diameter);
		button_done=(Button)this.findViewById(R.id.set_diameter);
		width=(EditText)this.findViewById(R.id.input_certain_width);
		height=(EditText)this.findViewById(R.id.input_certain_height);
		skip=(EditText)this.findViewById(R.id.skippixels);
		button_done.setOnClickListener(this);
		initSpinner(SettingActivity.BACK_CAM);
		initWorkingModeSpinner();
	}
	private void initWorkingModeSpinner(){
		workMode = (Spinner)this.findViewById(R.id.spotcountmode);
		modeList.add("Single Spot");
		modeList.add("Two Spot");
		modeAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, modeList);
		modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		workMode.setAdapter(modeAdapter);
		workMode.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int arg2, long arg3) {
				// TODO Auto-generated method stub
				detectCount = arg2 + 1;
				Toast.makeText(SettingActivity.this, detectCount + " mode", Toast.LENGTH_SHORT).show();
				arg0.setVisibility(View.VISIBLE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
	}
	private void initSpinner(int frontback ){
		mySpinner = (Spinner)this.findViewById(R.id.frontorback);
		list.add("back");
		list.add("front");
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		//第三步：为适配器设置下拉列表下拉时的菜单样式。
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//第四步：将适配器添加到下拉列表上
		mySpinner.setAdapter(adapter);
		mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int position, long arg3) {
				choice = position;
				Toast.makeText(SettingActivity.this, adapter.getItem(position)+" camera to use", Toast.LENGTH_SHORT).show();
				arg0.setVisibility(View.VISIBLE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view.getId()==R.id.set_diameter){
			String temp=et_diameter.getText().toString();
			String widths= width.getText().toString();
			String heights = height.getText().toString();
			String toskip= skip.getText().toString();
			if(temp!=null && !temp.equals("")){
				diameter=Double.valueOf(temp);
				Intent intent=new Intent();
				Class<SpotMonitorActivity> toactivity=SpotMonitorActivity.class;
				Class<SpotTwoActivity> toActivity2 = SpotTwoActivity.class;
				int request_code=REQUEST_DATA_SPOT_ONE;
				if(this.detectCount == SettingActivity.ONE_SPOT){
					intent.setClass(SettingActivity.this, toactivity);
					request_code=REQUEST_DATA_SPOT_ONE;
				}else if(this.detectCount == SettingActivity.TWO_SPOT){
					intent.setClass(SettingActivity.this, toActivity2);
					request_code=REQUEST_DATA_SPOT_TWO;
				}
				intent.putExtra("diameter_vaue", diameter);
				intent.putExtra("frontorback", choice);
				if(widths !=null && !widths.equals("")){
					int wd= Integer.valueOf(widths);
					intent.putExtra("width",wd);
				}
				if(heights !=null && !heights.equals("")){
					int ht= Integer.valueOf(heights);
					intent.putExtra("height",ht);
				}
				if(toskip !=null && !toskip.equals("")){
					int ts= Integer.valueOf(toskip);
					intent.putExtra("toskip",ts);
				}
				startActivityForResult(intent,request_code);
			}else{
				Toast.makeText(SettingActivity.this, "Please Input Diameter！", Toast.LENGTH_LONG).show();
			}

		}
	}

	//added by hongtao.fu for copy data begin
	public final static String savedSdPath = "/spotmoitor";

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Log.i("hongtao.fu2","onActivity Result");
		if(requestCode==REQUEST_DATA_SPOT_ONE || requestCode==REQUEST_DATA_SPOT_TWO) {
			String name="one_spot_monitor_data.txt";
			int dataType=FileDataEntity.TYPE_SPOT_ONE;
			if(requestCode==REQUEST_DATA_SPOT_TWO){
				name="two_spot_monitor_data.txt";
				dataType=FileDataEntity.TYPE_SPOT_TWO;
			}
			File dir = Environment.getExternalStorageDirectory();
			String path = dir.getPath() + savedSdPath + "/"+name;
			Log.i("hongtao.fu2","path="+path);
			File data = new File(path);
			Log.i("hongtao.fu2","path="+data.exists());
			if (data.exists()) {

				long size=FileUtil.getFileOrDirSize(data);
				Log.i("hongtao.fu2",name+" size="+size);
				if(size==0){
					Toast.makeText(this, "采集数据为空", Toast.LENGTH_SHORT).show();
					data.delete();
					return;
				}
				String userInfoJson = SPUtil.getValueByKey(getApplicationContext(), SPUtil.USER_INFO);
				Gson gson = new Gson();
				UserJson user = gson.fromJson(userInfoJson, UserJson.class);
				long time = System.currentTimeMillis();
				FileDataEntity fileDataEntity = new FileDataEntity
						(user.getId(),
								getIntent().getStringExtra(ProjectDataActivity.PROJECT_ID),
								"FILE_ID_" + Long.toHexString(time).toUpperCase(),//会在copyFileToProject的时候修改
								"spot_monitor_data.txt",//会在copyFileToProject的时候修改
								path,//会在copyFileToProject的时候修改
								dataType, 0,
								time//会在copyFileToProject的时候修改
						);
				boolean b = FileUtils.copyFileToProject(this, path, fileDataEntity);
				if (b) {
					Toast.makeText(this, "采集数据成功", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(this, "采集数据失败", Toast.LENGTH_SHORT).show();
				}
				data.delete();
			} else {
				Toast.makeText(this, "没有采集到数据", Toast.LENGTH_SHORT).show();
			}
		}
	}
	//added by hongtao.fu for copy data end
}
