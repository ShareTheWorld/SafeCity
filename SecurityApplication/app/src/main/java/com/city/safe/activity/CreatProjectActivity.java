package com.city.safe.activity;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.city.safe.R;
import com.city.safe.bean.UserJson;
import com.city.safe.dao.ProjectDB;
import com.city.safe.bean.ProjectEntity;
import com.city.safe.utils.SPUtil;
import com.city.safe.utils.TimeUtils;
import com.google.gson.Gson;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreatProjectActivity extends AppCompatActivity {

    @ViewInject(R.id.project_number)
    private TextView mProjectNumber;
    @ViewInject(R.id.project_time)
    private TextView mCreateTime;
    @ViewInject(R.id.project_name)
    EditText mProjectName;
    @ViewInject(R.id.project_desc)
    EditText mProjectDesc;
    @ViewInject(R.id.finish_button)
    Button mFinish;
    @ViewInject(R.id.cancle)
    Button mCancel;

    private String mNumber;
    private String mTimeText;
    private long mTime;
    private double mLatitude;
    private double mLongitude;
    public LocationClient mLocationClient;
    private TextView positionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_creat_project);
        positionText = (TextView)findViewById(R.id.project_create_position2);
        mCreateTime = (TextView)findViewById(R.id.project_time);
        x.view().inject(this);
        mTime=System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        mTimeText = simpleDateFormat.format(mTime);
        mCreateTime.setText(mTimeText);
        mNumber="ID_"+Long.toHexString(Calendar.getInstance().getTimeInMillis()).toUpperCase();
        mProjectNumber.setText(mNumber);
        requestLocation();
    }

    private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mLocationClient.stop();

    }
    public class MyLocationListener implements BDLocationListener{

        @Override
        public  void onReceiveLocation(BDLocation location){
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("(").append(location.getLatitude()).append(",");
            mLatitude= location.getLatitude();
            currentPosition.append(location.getLongitude()).append(")");
            mLongitude= location.getLongitude();
            positionText.setText(currentPosition);
        }
    }
    public void createFinish(View view){

        String p1 = mProjectName.getText().toString();
        String p2 = mProjectDesc.getText().toString();
        if(p1!= null && p2!= null && !"".equals(p1.trim()) && !"".equals(p2.trim())){

            String userInfoJson = SPUtil.getValueByKey(getApplicationContext(), SPUtil.USER_INFO);
            Gson gson = new Gson();
            UserJson user = gson.fromJson(userInfoJson, UserJson.class);

            ProjectEntity projectEntity=new ProjectEntity();
            projectEntity.setUser_id(user.getId());
            projectEntity.setProject_id(mNumber);
            projectEntity.setName(mProjectName.getText().toString().trim());
            projectEntity.setDesc(mProjectDesc.getText().toString().trim());
            projectEntity.setTime(mTime);
            projectEntity.setLocation(mLatitude+","+mLongitude);
            projectEntity.setState(0);
            ProjectDB.getIntance().insert(projectEntity);
                Toast.makeText(getApplicationContext(),"创建成功！",Toast.LENGTH_SHORT).show();
            Log.i("hongtao.fu2",projectEntity.toString());
            finish();
        }else{
            Toast.makeText(getApplicationContext(),"项目名称和简介不能为空！" ,Toast.LENGTH_SHORT).show();
        }
    }
    public void cancelFinish(View view){
        finish();
    }


}
