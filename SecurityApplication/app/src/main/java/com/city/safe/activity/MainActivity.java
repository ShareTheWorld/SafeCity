package com.city.safe.activity;

/**
 * Created by user on 16-12-10.
 */
/**
 * 　┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　　┃
 * ┃　　　━　　　┃
 * ┃　┳┛　┗┳　┃
 * ┃　　　　　　　┃
 * ┃　　　┻　　　┃
 * ┃　　　　　　　┃
 * ┗━┓　　　┏━┛
 * 　　┃　　　┃神兽保佑
 * 　　┃　　　┃代码无BUG！
 * 　　┃　　　┗━━━┓
 * 　　┃　　　　　　　┣┓
 * 　　┃　　　　　　　┏┛
 * 　　┗┓┓┏━┳┓┏┛
 * 　　　┃┫┫　┃┫┫
 * 　　　┗┻┛　┗┻┛
 */

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.city.safe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {


    private ListView mListView;
    private int icons[]=new int[]{R.drawable.myproject_icon,
            R.drawable.usermanagement_icon,
            R.drawable.sos_icon,
            R.drawable.aboutus_icon};
    private String texts[];


    private long exitTime = 0 ;
    public MapView mapView = null;
    public BaiduMap baiduMap = null;
    public LocationClient locationClient = null;
    BitmapDescriptor mCurrentMarker = null;
    boolean isFirstLoc = true;


    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if(location == null || mapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);

            if(isFirstLoc){
                isFirstLoc = false;

                LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,16);
                baiduMap.animateMapStatus(u);

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        mapView = (MapView) this.findViewById(R.id.map_view);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);

        mListView=(ListView)findViewById(R.id.main_lv);

        texts=new String[]{getString(R.string.myproject),
                getString(R.string.usermanagement),
                getString(R.string.sos),
                getString(R.string.aboutus)};


        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(myListener);
        this.setLocationOption();
        locationClient.start();

        List< Map<String,Object> > data= new ArrayList<Map<String,Object>>();
        for(int i=0;i<icons.length;i++){
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("icon",icons[i]);
            map.put("text",texts[i]);
            data.add(map);
        }
        String[] from=new String[]{"icon","text"};
        int[] to=new int[]{R.id.acc_iv,R.id.text_tv};
        SimpleAdapter adapter=new SimpleAdapter(this,data,R.layout.add_data_item,from,to);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=null;
                switch (position){
                    case 0:
                        intent=new Intent(MainActivity.this,ProjectActivity.class);
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this,UserManagementActivity.class);
                        break;
                    case 2:
                        ComponentName componentName = new ComponentName("com.android.phone","com.android.phone.EmergencyDialer");
                        intent = new Intent();
                        intent.setComponent(componentName);
                        break;
                    case 3:
                        intent = new Intent(MainActivity.this,AboutUsActivity.class);
                        break;

                    default:
                        break;
                }
                startActivity(intent);

            }
        });


    }

    @Override
    protected void onDestroy(){
        locationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        super.onDestroy();
        mapView.onDestroy();
        mapView = null;
    }

    @Override
    protected void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected  void onPause(){
        super.onPause();
        mapView.onPause();
    }

    private void setLocationOption(){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(true);
        locationClient.setLocOption(option);
    }

    @Override
    public  boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime)>2000){
                Toast.makeText(getApplicationContext(), R.string.twice_exit,Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else{
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    public void onFinish(View view){
        Intent intent = new Intent(MainActivity.this, CreatProjectActivity.class);
        startActivity(intent);
        finish();
    }
}
