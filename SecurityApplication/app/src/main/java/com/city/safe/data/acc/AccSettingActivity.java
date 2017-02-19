package com.city.safe.data.acc;



import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.city.safe.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccSettingActivity extends BaseActivity {

	private Button startBtn = null ;
	private EditText etDuration = null ;
	private EditText etFreq = null ;
	private EditText etFilename = null ;

	private String strDuration = null ;
	private String strFreq = null ;

	private String strFilePath = null ;
	//file
	FileService service = null;
	String strfilename = "tasknamefile.txt" ;
	String strparamname = "accparamfile.txt" ;
	private String strPhotoName = "" ;
	int nFreq = 0 ;
	String strPhotoPath = "";

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_acc_setting);

		initHead();
		btn_leftTop.setVisibility(View.GONE);
		btn_rightTop.setVisibility(View.GONE);
		tv_head.setText(R.string.acc_title);

		btn_leftTop.setOnClickListener(new BackListener()) ;
		btn_rightTop.setOnClickListener(new CameraListener()) ;

		startBtn = (Button) findViewById(R.id.btn_acc_start);
		startBtn.setOnClickListener(new StartListener()) ;
		etDuration = (EditText) findViewById(R.id.edt_acc_duration);
		etFreq = (EditText) findViewById(R.id.edt_acc_freq);
		etFilename = (EditText) findViewById(R.id.edt_acc_filename);

		service = new FileService(getApplicationContext()) ;

		String strParam = null;
		try {
			strParam = service.read(strparamname) ;
		} catch (Exception e) {
			return;
		}
		if(!strParam.isEmpty())
		{
			String[] sParam = strParam.split(",") ;
			if(sParam.length>1){
				etDuration.setText(sParam[0]);
				etFreq.setText(sParam[1]);
			}
		}

		Bundle bundle = this.getIntent().getExtras();
		//接收name值
		if(bundle!=null){
			strPhotoName = bundle.getString("photoname");
			if( strPhotoName!=null){
				strPhotoPath = Environment.getExternalStorageDirectory().toString() ;
				strPhotoPath = strPhotoPath+"/"+strPhotoName ;
				if(new File(strPhotoPath).exists()){
					Bitmap bmp = null ;
					ImageView image = (ImageView) findViewById(R.id.struct_image);
					try {
						FileInputStream fis = new FileInputStream(strPhotoPath);
						bmp = BitmapFactory.decodeStream(fis);
						image.setImageBitmap(bmp);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
			nFreq = bundle.getInt("freq") ;
			if( nFreq!=0 ){
				etFreq.setText(String.valueOf(nFreq));
				SaveParam();
			}
		}

	}

	private boolean SaveParam(){
		strDuration = etDuration.getText().toString();
		if(strDuration.isEmpty()){
			strDuration = String.valueOf(60) ;
		}
		strFreq =etFreq.getText().toString();
		if(strFreq.isEmpty()){
			strFreq = String.valueOf(100.0f);
		}
		String strParam = strDuration+(",")+strFreq+(",");
		try {
			service.savePrivate(strparamname,strParam) ;//覆盖保存
		} catch (Exception e) {
			Toast.makeText(AccSettingActivity.this,R.string.err_fileFailed, Toast.LENGTH_LONG).show();
			return false ;
		}
		return true ;
	}

	class BackListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
//			Intent intent = new Intent();
//			intent.setClass(AccSettingActivity.this, SensorMonitorActivity.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			//用Bundle携带数据
//			Bundle bundle=new Bundle();
//			//传递name参数
//			bundle.putInt("start", 1);
//			intent.putExtras(bundle);
//			startActivity(intent);
		}
	}
	class CameraListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
//			Intent intent = new Intent();
//			intent.setClass(AccSettingActivity.this, CameraPhotoActivity.class);
//			startActivity(intent);
		}
	}
	class StartListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			//保存配置参数 strparamname
			if(!SaveParam())
				return ;
			//保存任务文件
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			Date curDate = new Date(System.currentTimeMillis());//获取当前时间
			String str = formatter.format(curDate);
			StringBuffer strNameBuf = new StringBuffer(str) ;
			strNameBuf.append("_AccTextData.txt") ;
			String strFile = strNameBuf.toString() ;
			etFilename.setText(strFile) ;

			strNameBuf.append(",") ;
			strNameBuf.append(strPhotoName);

			strNameBuf.append("^") ;
			try {
				service.save(strfilename,strNameBuf.toString()) ;
			} catch (Exception e) {
				Toast.makeText(AccSettingActivity.this,R.string.err_fileFailed, Toast.LENGTH_LONG).show();
				return ;
			}

			Intent intent = new Intent();
			intent.setClass(AccSettingActivity.this,
					AccDataActivity.class);
			//用Bundle携带数据
			Bundle bundle=new Bundle();
			//传递name参数
			bundle.putString("times", strDuration);
			bundle.putString("freq", strFreq);
			bundle.putString("filename", strFile);
			intent.putExtras(bundle);
			startActivity(intent);
			finish();
		}
	}
}
