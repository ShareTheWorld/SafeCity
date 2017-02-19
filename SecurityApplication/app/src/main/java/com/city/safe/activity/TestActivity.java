package com.city.safe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.city.safe.R;
import com.city.safe.dao.FileDataDB;
import com.city.safe.dao.ProjectDB;
import com.city.safe.data.investigation.InvestigationActivity;
import com.city.safe.data.spotmonitor.SettingActivity;
import com.city.safe.http.PostHttp;
import com.city.safe.http.UploadFile;
import com.city.safe.utils.FileUtils;
import com.city.safe.view.NumberProgressBar;
import com.city.safe.view.OnProgressBarListener;

import org.xutils.common.Callback;
import org.xutils.db.annotation.Column;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by user on 16-12-5.
 */

public class TestActivity extends Activity implements OnProgressBarListener {
    private NumberProgressBar bnp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
//        x.Ext.init(getApplication()); // 这一步之后, 我们就可以在任何地方使用x.app()来获取Application的实例了.
//        x.Ext.setDebug(true); // 是否输出debug日志
        new Thread(){
            @Override
            public void run() {
//                UploadFile uf=new UploadFile();
//                //uf.uploadMultiFile("/sdcard/user/temp/sound","shake_sound_male.mp3");
//                Map<String,Object> map=new HashMap<String,Object>();
//                map.put("oq","abcdefghijklmn");
//                uf.uploadFile(map,new File("/sdcard/user/temp/sound/shake_sound_male.mp3"));
            }
        }.start();

//        bnp = (NumberProgressBar)findViewById(R.id.numberbar1);
//        bnp.setOnProgressBarListener(this);
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        bnp.incrementProgressBy(1);
//                    }
//                });
//            }
//        }, 1000, 100);

        ProjectDB projectDB=ProjectDB.getIntance();
//        number, String name, String desc, int state, List<String> data
//        ProjectEntity projectEntity=new ProjectEntity("333333","Project two","this is two project",2,"path1,path2".getBytes());
//        projectDB.insert(projectEntity);
//        List<ProjectEntity> l1=projectDB.select();
//        if(l1!=null) {
//            Logger.i("hongtao.fu", "" + l1);
//        }else{
//            Logger.i("hongtao.fu","l1 is null");
//        }

//        FileDataEntity fileDataEntity=new FileDataEntity("1234323","name.txt","ho;n\'g\'t\"ao.\"fu".getBytes(),0);
//        FileDataDB fileDataDB=FileDataDB.getIntance();
//        fileDataDB.insert(fileDataEntity);
//        List<FileDataEntity> l2=fileDataDB.select();
//        if(l2!=null) {
//            Logger.i("hongtao.fu", "" + l2);
//        }else{
//            Logger.i("hongtao.fu","l2 is null");
//        }

    }

    @Override
    public void onProgressChange(int current, int max) {
        if(current == max) {
//            Toast.makeText(getApplicationContext(), getString(R.string.finish), Toast.LENGTH_SHORT).show();
//            bnp.setProgress(0);
        }
    }
    public void callFun(View view){
//        Intent intent = new Intent(this, InvestigationActivity.class);
//        startActivity(intent);

//        PostHttp postHttp=new PostHttp(getApplicationContext());
//        Map<String,String> map=new HashMap<>();
//        map.put("email","123456789@qq.com"); //email: 123456789@qq.com, password: 123456789
//        map.put("password","123456789");
//        postHttp.login(map,null);
//        PostHttp postHttp=new PostHttp(getApplicationContext());
//
//
//        Map<String,String> map=new HashMap<>();
//        map.put("id","1024");
//        map.put("user_id","user_id_232");
//        map.put("project_id","id_23");
//        map.put("file_id","file_id_132");
//        map.put("email","123456789@qq.com");
//        map.put("password","123456789");
//        map.put("name","fileName");
////        map.put("path","/locate/sdb/ls.txt");
//        map.put("type","1");
//        map.put("state","0");
//        map.put("time",System.currentTimeMillis()+"");
//        postHttp.uploadFile(map,new Callback.CommonCallback<String>(){
//            @Override
//            public void onSuccess(String result) {
//                Log.i("hongtao.fu2","onSuccess=   "+result);
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                Log.i("hongtao.fu2","onError="+ex.getMessage());
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//                Log.i("hongtao.fu2","onCancelled");
//            }
//
//            @Override
//            public void onFinished() {
//                Log.i("hongtao.fu2","onFinished");
//            }
//        },null);



    }

//    @Event(value = R.id.id_bt_post)
    public void postFht(View v) {

        //FileUtils.copyFileToProject(getApplicationContext(),"/sdcard/user/temp/sound/shake_sound_male.mp3");

    }

    public void callSpot(View view){
//        Intent intent = new Intent(this, SettingActivity.class);
//        startActivity(intent);
    }
}
