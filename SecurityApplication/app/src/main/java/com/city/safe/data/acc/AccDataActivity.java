package com.city.safe.data.acc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.city.safe.R;
import com.city.safe.activity.ProjectDataActivity;
import com.city.safe.bean.FileDataEntity;
import com.city.safe.bean.UserJson;
import com.city.safe.dao.FileDataDB;
import com.city.safe.data.investigation.InvestigationActivity;
import com.city.safe.utils.FileUtils;
import com.city.safe.utils.SPUtil;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AccDataActivity extends BaseActivity {
	private Handler handler;
	private TimerTask task;
	private Timer timer = new Timer();//定时器

	//sensor
	boolean bSensorRegistSucc = false ;
	private SensorManager mSensorManager = null;
	private long  lCount = 0 ;
	private float fXValue = 0 ;
	private float fXmax = 0 ;
	private float fXmin = 0 ;
	private float fYValue = 0 ;
	private float fYmax = 0 ;
	private float fYmin = 0 ;
	private float fZValue = 0 ;
	private float fZmax = 0 ;
	private float fZmin = 0 ;

	private float fZLastValue = -100.0f ;

	private String strfileName = "test.txt";
	private String strFilePath = null ;

	String strSavePath = "";//Environment.getExternalStorageDirectory().toString() ;

	//face
	private TextView	tvXValu = null ;
	private TextView	tvXValuAvg = null ;
	private TextView	tvXValuMin = null ;
	private TextView	tvXValuMax = null ;

	private TextView	tvYValu = null ;
	private TextView	tvYValuAvg = null ;
	private TextView	tvYValuMin = null ;
	private TextView	tvYValuMax = null ;

	private TextView	tvZValu = null ;
	private TextView	tvZValuAvg = null ;
	private TextView	tvZValuMin = null ;
	private TextView	tvZValuMax = null ;
	//time
	private long	lStartTime = 0 ;
	private long	lEndTime = 0 ;
	private long	lStartTime1 = 0 ;
	private int		nSencods = 0 ;
	private	boolean bExit = false ;

	private int		nTimeLen = 0 ;
	private float	fFreqSet = 0f ;
	private int		nTimeSpace = 10 ;
	//file
	FileService service = null;
	String strfilename = "tasknamefile.txt" ;

	StringBuffer strBuf = new StringBuffer("");

	Lock lock = new ReentrantLock();
	private Button mFinshButton;
	private Button mCancelButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_acc_data);

		initHead();

		btn_leftTop.setVisibility(View.GONE);
		btn_rightTop.setVisibility(View.GONE);
		tv_rightTop.setVisibility(View.VISIBLE);

		mFinshButton=(Button)findViewById(R.id.finish_button);
		mCancelButton=(Button)findViewById(R.id.cancel_button);
		mFinshButton.setOnClickListener(new BackListener());
		mCancelButton.setOnClickListener(new BackListener());
		tv_head.setText(R.string.acc_data);

		btn_leftTop.setOnClickListener(new BackListener()) ;

		tvXValu = (TextView) findViewById(R.id.xvalue);
		tvXValuAvg = (TextView) findViewById(R.id.xavg);
		tvXValuMin = (TextView) findViewById(R.id.xmin);
		tvXValuMax = (TextView) findViewById(R.id.xmax);
		tvYValu = (TextView) findViewById(R.id.yvalue);
		tvYValuAvg = (TextView) findViewById(R.id.yavg);
		tvYValuMin = (TextView) findViewById(R.id.ymin);
		tvYValuMax = (TextView) findViewById(R.id.ymax);
		tvZValu = (TextView) findViewById(R.id.zvalue);
		tvZValuAvg = (TextView) findViewById(R.id.zavg);
		tvZValuMin = (TextView) findViewById(R.id.zmin);
		tvZValuMax = (TextView) findViewById(R.id.zmax);

		// 新页面接收数据
		Bundle bundle = this.getIntent().getExtras();
		// 接收name值
		if (bundle != null) {
			strfileName = bundle.getString("filename");
			nTimeLen = Integer.valueOf(bundle.getString("times"));
			fFreqSet = Float.valueOf(bundle.getString("freq"));
			// Toast.makeText(AccDataActivity.this,strfileName,
			// Toast.LENGTH_LONG).show();
			nTimeSpace = 1000 ;// / (int) fFreqSet;
		}

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				// 存储数据
				if (lStartTime == 0) {
					lStartTime1 = lStartTime = System.currentTimeMillis();
				}
				if (bExit == false) {
					long lCurrentTime = System.currentTimeMillis();

					int nCurTime = ++nSencods;
					if (lCurrentTime - lStartTime1 >= 1) {
						lStartTime1 = lCurrentTime;
						tv_rightTop.setText(String.valueOf(nCurTime) + "s");

						//算出最大最小值
						if(fXmin>fXValue){
							fXmin = fXValue;
						}
						if(fXmax<fXValue){
							fXmax=fXValue;
						}
						if(fYmin>fYValue){
							fYmin = fYValue;
						}
						if(fYmax<fYValue){
							fYmax=fYValue;
						}
						if(fZmin>fZValue){
							fZmin = fZValue;
						}
						if(fZmax<fZValue){
							fZmax=fZValue;
						}

						// 界面数据展示：

//						tvXValu.setText(String.valueOf(fXValue));
//						tvYValu.setText(String.valueOf(fYValue));
//						tvZValu.setText(String.valueOf(fZValue));
//
//						tvXValuAvg.setText("Avg:"
//								+ String.valueOf((fXmax - fXmin) / 2));
//						tvXValuMax.setText("Max:" + String.valueOf((fXmax)));
//						tvXValuMin.setText("Min:" + String.valueOf((fXmin)));
//
//						tvYValuAvg.setText("Avg:"
//								+ String.valueOf((fYmax - fYmin) / 2));
//						tvYValuMax.setText("Max:" + String.valueOf((fYmax)));
//						tvYValuMin.setText("Min:" + String.valueOf((fYmin)));
//
//						tvZValuAvg.setText("Avg:"
//								+ String.valueOf((fZmax - fZmin) / 2));
//						tvZValuMax.setText("Max:" + String.valueOf((fZmax)));
//						tvZValuMin.setText("Min:" + String.valueOf((fZmin)));

					}
					// strBuf.append(strCon);
					if (nCurTime >= nTimeLen) {
						bExit = true;
					}
				}
			}
		};

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		service = new FileService(getApplicationContext()) ;

		//Toast.makeText(AccSettingActivity.this,"jpg is exist", Toast.LENGTH_LONG).show();


		//start timer
		task = new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				message.what = 100;
				handler.sendMessage(message);
			}
		};
	}

	private SensorEventListener mSensorListener = new SensorEventListener() {
		//@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}

		@SuppressWarnings("static-access")
		//@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.TYPE_LINEAR_ACCELERATION == Sensor.TYPE_LINEAR_ACCELERATION) {
				float[] values = event.values;
				lock.lock() ;
				fXValue = values[0] ;
				fYValue = values[1] ;
				fZValue = values[2] ;
				lock.unlock();
				// 加速度高于10后开始时间计数
				if( lCount==0 ){
					fXmin = fXmax = fXValue;
					fYmin = fYmax = fYValue;
					fZmin = fZmax = fZValue;
					timer.schedule(task, 0, nTimeSpace); // timer
				}

				long lCurrentTime = System.currentTimeMillis();
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy MM dd HH mm ss.SSS");
				Date curDate = new Date(lCurrentTime);// 获取当前时间
				String strTime = formatter.format(curDate);

				StringBuffer sb = new StringBuffer(strTime) ;
				sb.append(" ") ;
				sb.append(fXValue) ;
				sb.append(" ") ;
				sb.append(fYValue) ;
				sb.append(" ") ;
				sb.append(fZValue) ;
				sb.append("\n");
				if (bExit){
					lEndTime = lCurrentTime ;
					String strBuffer = new String(strBuf.toString());
					SaveAndExit(strBuffer);
					mSensorManager.unregisterListener(mSensorListener);
				}else{
					lCount++;
					strBuf.append(sb);
				}
			}
		}
	};

	private boolean SaveValeToFile(String strContent) throws IOException {
		// TODO Auto-generated method stub
		//存入程序内部文件
		strfileName="acc_text_data.txt";//fix file name
		try {
			service.save(strfileName,strContent) ;
		} catch (Exception e) {
			Toast.makeText(AccDataActivity.this,R.string.err_fileFailed, Toast.LENGTH_LONG).show();
			return  false;
		}

		//存root目录
		String sDir = Environment.getExternalStorageDirectory().toString()+"/orioncc";
		File destDir = new File(sDir);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		File picFile = new File(sDir,strfileName) ;
		Log.i("hongtao.fu2",picFile.getPath()+" ");
		FileOutputStream pStream = null ;
		try {
			pStream = new FileOutputStream(picFile);
			pStream.write(strContent.getBytes());
			pStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return true ;
	}

	void SaveAndExit(String strBuf) {
		// TODO Auto-generated method stub
		if(bSensorRegistSucc){
			bExit = true;
			timer.cancel() ;
			try {
				SaveValeToFile(strBuf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		finish();
//		Intent intent = new Intent();
//		intent.setClass(AccDataActivity.this, AccSettingActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		//用Bundle携带数据
//		Bundle bundle=new Bundle();
//		//传递name参数
//		long nFreq = lCount/((lEndTime-lStartTime)/1000);
//		bundle.putInt("freq", (int)nFreq);
//		intent.putExtras(bundle);
//		startActivity(intent);
	}

	class BackListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.finish_button:
					SaveAndExit(strBuf.toString()) ;
					moveFileToCitySafeProject();
					break;
				case R.id.cancel_button:
					finish();
					break;
				default:
			}

		}
	}

	@Override
	protected void onResume() {
		bSensorRegistSucc = mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				SensorManager.SENSOR_DELAY_FASTEST); // SENSOR_DELAY_UI
		// SENSOR_DELAY_FASTEST
		if (!bSensorRegistSucc) {
			bSensorRegistSucc = mSensorManager.registerListener(
					mSensorListener,
					mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_FASTEST);
			if (!bSensorRegistSucc) {
				Toast.makeText(AccDataActivity.this, R.string.acc_reg_err,
						Toast.LENGTH_LONG).show();
				SaveAndExit("");
			}
		}

		super.onResume();
	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(mSensorListener);
		super.onPause();
	}
	public void moveFileToCitySafeProject() {
		long time = System.currentTimeMillis();
		String userInfoJson = SPUtil.getValueByKey(getApplicationContext(), SPUtil.USER_INFO);
		Gson gson = new Gson();
		UserJson user = gson.fromJson(userInfoJson, UserJson.class);
		String name = "acc_text_data.txt";
		String path = Environment.getExternalStorageDirectory().toString() + "/orioncc/" + name;
		File file = new File(path);
		if(file.exists()) {
			if(file.length()>0) {
				FileDataEntity fileDataEntity = new FileDataEntity
						(user.getId(),
								SPUtil.getValueByKey(this, SPUtil.CURRENT_PROJECT_ID),
								"FILE_ID_" + Long.toHexString(time).toUpperCase(),
								name,
								path,
								FileDataEntity.TYPE_ACC, 0,
								time
						);
				boolean b = FileUtils.copyFileToProject(this, path, fileDataEntity);
				if (b) {
					Toast.makeText(this, "文件添加成功", Toast.LENGTH_SHORT).show();
					Log.i("hongtao.fu2","acc 文件添加成功");
					boolean m=file.delete();
					Log.i("hongtao.fu2","m="+m);
					finish();
				} else {
					Toast.makeText(this, "文件添加失败", Toast.LENGTH_SHORT).show();
					Log.i("hongtao.fu2","acc 文件添加失败");
				}
			}else{
				Toast.makeText(this, "采取到的数据为空", Toast.LENGTH_SHORT).show();
				Log.i("hongtao.fu2","acc 采取到的数据为空");
			}

		}else{
			Toast.makeText(this,"采集数据失败",Toast.LENGTH_SHORT).show();
			Log.i("hongtao.fu2","acc 采集数据失败");
		}
	}


}
