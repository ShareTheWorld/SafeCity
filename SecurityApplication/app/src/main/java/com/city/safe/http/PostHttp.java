package com.city.safe.http;

import android.content.Context;
import android.util.Log;

import org.xutils.HttpManager;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.Map;

/**
 * Created by user on 16-12-24.
 */

public class PostHttp {
    public static final String REGISTER_URI="http://115.28.12.16/web_forMrzhao/index.php/Index/Login/handle";
    public static final String LOGIN_URI="http://115.28.12.16/web_forMrzhao/index.php/Index/Login/userLogin";
    public static final String LOGOUT_URI="http://115.28.12.16/web_forMrzhao/index.php/Index/Login/logout";
    public static final String CREATE_PROJECT_URI="http://115.28.12.16/web_forMrzhao/index.php/Index/Login/newProject";
    public static final String UPLOAD_FILE_URI="http://115.28.12.16/web_forMrzhao/index.php/Index/Login/uploadFile";
    public static final String MODIFY_INFO="http://115.28.12.16/web_forMrzhao/index.php/Index/Login/modifyUserData";
    public Context mContext;
    private  Callback.CommonCallback<String> mCallback;
    public PostHttp(Context context) {
        this.mContext=context;
    }
    public void init(){

    }

    /**
     * 注册
     * @param map
     * @param callback
     */
    public void register(Map<String,String> map,Callback.CommonCallback<String> callback){
        RequestParams params = new RequestParams(REGISTER_URI);
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                params.addParameter(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
        }
        HttpManager httpManager= x.http();
        httpManager.post(params, callback);
    }

    /**
     * 登录
     * @param map
     * @param callback
     */
    public void login(Map<String, String> map,Callback.CommonCallback<String> callback){
        RequestParams params = new RequestParams(LOGIN_URI);
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                params.addParameter(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
        }
        Log.i("hongtao.fu2",params.toString());

        HttpManager httpManager=x.http();
        httpManager.post(params,callback);
    }
    public void logout(){

    }
    public void createProject(Map<String, String> map,Callback.CommonCallback<String> callback){
        RequestParams params = new RequestParams(CREATE_PROJECT_URI);
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                params.addParameter(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
        }
        Log.i("hongtao.fu2",params.toString());
        HttpManager httpManager=x.http();
        httpManager.post(params,callback);
    }
    public void uploadFile(Map<String, String> map,Callback.ProgressCallback<String> callback,File file){
        RequestParams params = new RequestParams(UPLOAD_FILE_URI);
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                params.addParameter(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
        }
        params.setMultipart(true);//"/sdcard/user/temp/sound/shake_sound_male.mp3"
        params.addBodyParameter("file",file);
        Log.i("hongtao.fu2",params.toString());
        HttpManager httpManager=x.http();
        httpManager.post(params,callback);
    }
    /**
     * 密码修改
     * @param map
     * @param callback
     */
    public void modifyInfo(Map<String, String> map,Callback.CommonCallback<String> callback){
        RequestParams params = new RequestParams(MODIFY_INFO);
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                params.addParameter(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
        }
        Log.i("hongtao.fu2",params.toString());

        HttpManager httpManager=x.http();
        httpManager.post(params,callback);
    }


}
