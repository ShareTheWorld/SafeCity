package com.city.safe.data.spotmonitor;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.city.safe.R;

/**
 * detection view with some adjustment button and location
 * @author peter
 *
 */
public class SpotMonitorActivity extends Activity implements CvCameraViewListener2, OnClickListener {
	private static final String  TAG  = "SpotMonitorActivity";

	private Mat    mRgba;
	private int  camerachoice = SettingActivity.BACK_CAM;
	public static int threshhold=220;
	public  int white_bg_black_thresh=100;
	public static int white_bg_red_thresh=235;
	public  int black_bg_red_thresh=100;

	private CameraBridgeViewBase mOpenCvCameraView;
	private TextView result;
	private Button start;
	private Button indentify;
	private Button switch_env;
	private TextView threshValueView;
	private ImageButton upButton;
	private ImageButton downButton;

	private View controlPanel;
	private ImageButton upMove;
	private ImageButton leftMove;
	private ImageButton rightMove;
	private ImageButton downMove;

	private int shapeWidth = 30;  //in mm with default value
	private int shapeHeight =10;  //in mm
	private int shapeWidthInPixels = 0;
	private int shapeHeightInPixels = 0;
	private int upperLeftx = 0;
	private int upperLefty = 0;
	private int onceMoveInPixels = 10;

	private TextView detectThreshValue;
	private ImageButton detectupButton;
	private ImageButton detectdownButton;

	Mat grayimg;
	Mat threshimg;
	Handler handler=new MyHandler();
	private double diameter;
	private double rate;         // ? mms per pixel
	private int status=-1;  //-1: default ; 0: indentify; 1: monitor;
	private int pc;
	private int stubx;
	private int stuby;
	private Date imageTime = null;  //when the image come

	private int toskip = 1;
	private int workingMode = GlobalData.ModeDefault;

	private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
				case LoaderCallbackInterface.SUCCESS:
				{
					Log.i(TAG, "OpenCV loaded successfully");
					mOpenCvCameraView.enableView();
				} break;
				default:
				{
					super.onManagerConnected(status);
				} break;
			}
		}
	};

	public class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 88){
				result.setText(""+msg.arg1);
				return;
			}
			if(msg.what==8){
				Toast.makeText(getApplicationContext(), "Ratio:"+diameter+"/"+pc+"="+rate, Toast.LENGTH_LONG).show();
				shapeWidthInPixels = (int)( shapeWidth/rate);
				Log.e(TAG, "width(mm)"+ shapeWidth+ "pixels"+ shapeWidthInPixels);
				shapeHeightInPixels = (int)(shapeHeight/rate);
				if(shapeWidthInPixels >= width){
					shapeWidthInPixels = width/4;
					shapeHeightInPixels  = shapeWidthInPixels/3;
				}else if(shapeHeightInPixels >= height){
					shapeHeightInPixels = shapeWidthInPixels/3;
				}
				chooseWorkMode();
				return;
			}
			String str="";
			if(msg.arg1!=-10000){
				str="Counts："+imagenum+" Coordinates："+"x:"+(msg.arg1)*rate+" y:"+(msg.arg2)*rate;
				result.setText(str);
				java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd hh mm ss.SSS");
				String date = format1.format(imageTime);
				saveToFile(imagenum+" "+date+" "+(msg.arg1)*rate+" "+(msg.arg2)*rate+"\r\n");
			}
		}
		public void saveToFile(String str){
			if(fs!=null){
				fs.saveRecord(str);
			}
		}
	}
	private void chooseWorkMode(){
		new AlertDialog.Builder(this).setTitle("Choose Detection Mode").setIcon(
				android.R.drawable.ic_dialog_info).setSingleChoiceItems(
				new String[] { "Black Circle", "Laser Spot" }, workingMode,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch(which){
							case 0: workingMode = GlobalData.ModeDarkDetection;break;
							case 1: workingMode = GlobalData.ModeLightDetection;break;
							default: workingMode = GlobalData.ModeDefault;break;
						}
						dialog.dismiss();
					}
				}).show();
	}
	FileSaver fs;
	public void createFile(){
		if(fs!=null){
			fs.close();
			fs=null;
		}
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String filename = formatter.format(currentTime);
		fs=new FileSaver("one_spot_monitor_data"+".txt");
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if(fs!=null) {
			fs.close();
		}
		if(bitmap!=null)
			bitmap.recycle();
		super.onStop();
	}
	public  SpotMonitorActivity() {
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.light_detection_surface_view);
		getDiameter();
		initView();
		new FileHelper();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK,null);
		this.finish();
	}

	private void initView(){
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
		mOpenCvCameraView.setCameraChoice(camerachoice);
		result=(TextView)this.findViewById(R.id.position_show);
		start=(Button)this.findViewById(R.id.start);
		start.setOnClickListener(this);
		indentify=(Button)this.findViewById(R.id.indentify_button);
		indentify.setOnClickListener(this);
		switch_env=(Button)this.findViewById(R.id.switch_env);
		switch_env.setOnClickListener(this);
		threshValueView=(TextView)this.findViewById(R.id.threshvalue);
		upButton = (ImageButton)this.findViewById(R.id.plus);
		downButton = (ImageButton)this.findViewById(R.id.minus);
		upButton.setOnClickListener(this);
		downButton.setOnClickListener(this);
		detectThreshValue = (TextView)this.findViewById(R.id.d_threshvalue);
		detectupButton =(ImageButton)this.findViewById(R.id.d_plus);
		detectupButton.setOnClickListener(this);
		detectdownButton = (ImageButton)this.findViewById(R.id.d_minus);
		detectdownButton.setOnClickListener(this);
		controlPanel = this.findViewById(R.id.controlpanel);
		upMove = (ImageButton)this.findViewById(R.id.up);
		leftMove = (ImageButton)this.findViewById(R.id.left);
		rightMove= (ImageButton)this.findViewById(R.id.right);
		downMove = (ImageButton)this.findViewById(R.id.down);
		upMove.setOnClickListener(this);
		leftMove.setOnClickListener(this);
		rightMove.setOnClickListener(this);
		downMove.setOnClickListener(this);
	}
	private void getDiameter(){
		Intent i=this.getIntent();
		if(i!=null){
			diameter=i.getExtras().getDouble("diameter_vaue");
			camerachoice = i.getExtras().getInt("frontorback");
			try{
				shapeWidth = i.getExtras().getInt("width", 30);
				shapeHeight = i.getExtras().getInt("height", 10);
				toskip= i.getExtras().getInt("toskip", 1) +1;
			}catch(Exception e){
			}
		}
	}
	protected void AllocateCache()
	{
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	}
	@Override
	public void onPause()
	{
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	public void onCameraViewStopped() {
		//mRgba.release();
	}
	private int imagenum;
	private int i=1;
	private long starttime;
	private long endtime;
	private byte thresh=0;
	private Bitmap bitmap;
	private int width=0;
	private int height=0;
	private boolean firstimage=true;
	private boolean firstlocation=true;
	private int[] pixels;
	private void identifyRate(){
		int i;
		int j;
		int maxPline=0;
		for (i = 0; i < height; i++) {
			int currentBlackP=0;
			for (j = 0; j < width; j++) {
				// 获得像素的颜色
				int color = pixels[width * i + j];
				int red = ((color & 0x00FF0000) >> 16);
				int green = ((color & 0x0000FF00) >> 8);
				int blue = color & 0x000000FF;
				color = (red + green + blue)/3;
				if(color<white_bg_black_thresh){
					currentBlackP++;
				}
			}
			if(currentBlackP>maxPline){
				maxPline=currentBlackP;
			}
		}
		if(maxPline!=0){
			rate=diameter/maxPline;
			status=-1;
			pc=maxPline;
			handler.sendEmptyMessage(8);
		}
	}
	private void spotLight(Mat threshing){
		Bitmap  temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(threshing, temp);
		int i;
		try{
			//Log.e(TAG, "w: "+ shapeWidthInPixels + "h:" + shapeHeightInPixels);
			for(i = 0 ; i<shapeHeightInPixels ; i++){
				temp.setPixel(upperLefty,upperLeftx + i,  Color.WHITE);
				temp.setPixel(upperLefty + shapeWidthInPixels, upperLeftx + i, Color.WHITE);
			}
			for( i = 0 ;i<shapeWidthInPixels; i++){
				temp.setPixel( upperLefty + i, upperLeftx, Color.WHITE);
				temp.setPixel(upperLefty + i, upperLeftx + shapeHeightInPixels ,  Color.WHITE);
			}
		}catch(Exception e){
			Log.e(TAG, e.toString());
		}
		Utils.bitmapToMat(temp, threshing);
	}
	public Mat onCameraFrame(CvCameraViewFrame inputFrame){
		imageTime = new Date();
		if(grayimg==null || threshimg==null){
			grayimg=new Mat();
			threshimg=new Mat();
		}
		grayimg=inputFrame.gray();
		if(firstimage){
			width=grayimg.width();
			height=grayimg.height();
			pixels=new int[width*height];
			AllocateCache();
			firstimage=false;
			//the rectangle upper left position
			upperLeftx = (height) /4 ;
			upperLefty = (width)/4;
		}
		if(status==0){
			Utils.matToBitmap(inputFrame.rgba(), bitmap);
			bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
			identifyRate();
			return threshimg;
		}else if(status==-1){
			Imgproc.threshold(grayimg, threshimg, white_bg_black_thresh, 255, Imgproc.THRESH_BINARY);
			return threshimg;
		}else if(status==2){
			spotLight(grayimg);
			return grayimg;
		}
		Imgproc.threshold(grayimg, threshimg, black_bg_red_thresh, 255, Imgproc.THRESH_BINARY);
		Utils.matToBitmap(inputFrame.rgba(), bitmap);
		bitmap.getPixels(pixels, upperLeftx * width, width, 0, upperLeftx, width, shapeHeightInPixels);
		int count=0;
		int i;
		int j;
		int sumx=0;
		int sumy=0;
		int color;
		int red;
		int green;
		int blue;

		int clipWidth = upperLefty + shapeWidthInPixels;
		int clipHeight = upperLeftx + shapeHeightInPixels;
		int detection_thresh = white_bg_black_thresh;
		if(workingMode  == GlobalData.ModeDarkDetection){
			detection_thresh = white_bg_black_thresh;
			for (i = upperLeftx; i < clipHeight; i++) {
				for (j = upperLefty; j < clipWidth; j+=toskip) {
					// 获得像素的颜色
					color = pixels[width * i + j];
					red = ((color & 0x00FF0000) >> 16);
					green = ((color & 0x0000FF00) >> 8);
					blue = color & 0x000000FF;
					color = (red + green + blue)/3;
					if(color < detection_thresh){
						sumx+=j;
						sumy+=i;
						++count;
						continue;
					}
				}
			}
		}else if(workingMode == GlobalData.ModeLightDetection){
			detection_thresh = black_bg_red_thresh;
			for (i = upperLeftx; i < clipHeight; i++) {
				for (j = upperLefty; j < clipWidth; j+=toskip) {
					// 获得像素的颜色
					color = pixels[width * i + j];
					red = ((color & 0x00FF0000) >> 16);
					green = ((color & 0x0000FF00) >> 8);
					blue = color & 0x000000FF;
					color = (red + green + blue)/3;
					if(color > detection_thresh){
						sumx+=j;
						sumy+=i;
						++count;
						continue;
					}
				}
			}
		}
		Message msg=new Message();
		msg.arg1=-10000;
		msg.arg2=-10000;
		if(count > 0){
			int centerx=sumx/count;
			int centery=sumy/count;
			if(firstlocation){
				stubx=centerx;
				stuby=centery;
				firstlocation=false;
			}else{
				imagenum++;
				msg.arg1=centerx-stubx;
				msg.arg2=centery-stuby;
			}

		}else{
			msg.arg1= 111111;
			msg.arg2= 111111;
			imagenum++;
		}
		handler.sendMessage(msg);
		return threshimg;
	}
	@Override
	public void onClick(View arg0) {
		if(arg0.getId()==R.id.start){
			controlPanel.setVisibility(View.GONE);
			if(start.getText().equals("stop")){
				status=-1;
				if(fs!=null) {
					fs.close();
				}
				start.setText("start");
				return;
			}
			if(diameter<=0 || pc==0 || rate==0){
				Toast.makeText(getApplicationContext(), "比率未识别成功，请重新识别", Toast.LENGTH_LONG).show();
				return;
			}
			start.setText("stop");
			status=1;
			imagenum=0;
			createFile();
		}else if(arg0.getId()==R.id.indentify_button){
			controlPanel.setVisibility(View.GONE);
			status=0;
		}else if(arg0.getId()==R.id.switch_env){
			controlPanel.setVisibility(View.VISIBLE);
			status=2;
		}else if(arg0.getId() == R.id.plus){
			int currentValue= Integer.valueOf( threshValueView.getText().toString());
			currentValue += 5;
			this.white_bg_black_thresh = currentValue;
			threshValueView.setText(""+currentValue);
		}else if(arg0.getId() == R.id.minus){
			int currentValue= Integer.valueOf( threshValueView.getText().toString());
			currentValue -= 5;
			this.white_bg_black_thresh = currentValue;
			threshValueView.setText(""+currentValue);
		}else if(arg0.getId() == R.id.d_plus){
			int currentValue = Integer.valueOf(this.detectThreshValue.getText().toString());
			currentValue += 5;
			this.black_bg_red_thresh = currentValue;
			this.detectThreshValue.setText(""+currentValue);
		}else if(arg0.getId() == R.id.d_minus){
			int currentValue = Integer.valueOf(this.detectThreshValue.getText().toString());
			currentValue -= 5;
			this.black_bg_red_thresh = currentValue;
			this.detectThreshValue.setText(""+currentValue);
		}else if(arg0.getId() == R.id.up){
			int temp = upperLeftx - onceMoveInPixels;
			if(temp < 0){
				Toast.makeText(this, "out of bound!", Toast.LENGTH_SHORT).show();
			}else{
				upperLeftx = temp;
			}
		}else if(arg0.getId() == R.id.left){
			int temp = upperLefty - onceMoveInPixels;
			if(temp<0){
				Toast.makeText(this, "out of bound!", Toast.LENGTH_SHORT).show();
			}else{
				upperLefty = temp;
			}
		}else if(arg0.getId() == R.id.right){
			int temp = upperLefty + onceMoveInPixels;
			if(temp > width){
				Toast.makeText(this, "out of bound!", Toast.LENGTH_SHORT).show();
			}else{
				upperLefty = temp;
			}
		}else if(arg0.getId() == R.id.down){
			int temp = upperLeftx + onceMoveInPixels;
			if(temp > height){
				Toast.makeText(this, "out of bound!", Toast.LENGTH_SHORT).show();
			}else{
				upperLeftx = temp;
			}
		}

	}
	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub

	}

}
