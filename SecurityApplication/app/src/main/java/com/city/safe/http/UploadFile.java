package com.city.safe.http;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by user on 16-12-5.
 */

public class UploadFile {
    private static final String TAG = "hongtao.fu2";
    private final String URL = "http://115.28.12.16/web_forMrzhao/index.php/Index/Login/uploadAccel";
    public Context mContext;

    public void uploadFile(Map<String, String> map, File file) {
        OkHttpClient client = new OkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder multipartBody_Body = new MultipartBody.Builder().setType(MultipartBody.FORM);
        //增加需要传递的参数
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                multipartBody_Body.addFormDataPart(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
        }

        RequestBody requestBody = null;
        //增加需要上传的文件
        if (file != null) {
            // MediaType.parse() 里面是上传的文件类型。
            requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            multipartBody_Body.addFormDataPart("accel", filename, requestBody);
        }

        Log.i("hongtao.fu2","requestBody"+requestBody);
        Request request=null;
        if(file==null) {
             request = new Request.Builder().url(URL).post(multipartBody_Body.build()).tag(mContext).build();
        }else {
             request = new Request.Builder()
                    .url(URL)
                    .post(ProgressHelper.addProgressRequestListener(requestBody, uiProgressRequestListener))
                    .tag(mContext)
                    .build();
        }
        // readTimeout("请求超时时间" , 时间单位);
        OkHttpClient okHttpClient = client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("hongtao.fu2", "onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String str = response.body().string();
                    Log.i("hongtao.fu2", response.message() + " , body " + str);
                } else {
                    Log.i("hongtao.fu2", response.message() + " error : body " + response.body().string());
                }
            }
        });
    }

    //这个是ui线程回调，可直接操作UI
    final UIProgressListener uiProgressRequestListener = new UIProgressListener() {
        @Override
        public void onUIProgress(long bytesWrite, long contentLength, boolean done) {
            Log.e("TAG", "bytesWrite:" + bytesWrite);
            Log.e("TAG", "contentLength" + contentLength);
            Log.e("TAG", (100 * bytesWrite) / contentLength + " % done ");
            Log.e("TAG", "done:" + done);
            Log.e("TAG", "================================");
            //ui层回调
//            uploadProgress.setProgress((int) ((100 * bytesWrite) / contentLength));
            Log.i("hongtao.fu2","progress="+(int) ((100 * bytesWrite) / contentLength)+"%");
            //Toast.makeText(getApplicationContext(), bytesWrite + " " + contentLength + " " + done, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onUIStart(long bytesWrite, long contentLength, boolean done) {
            super.onUIStart(bytesWrite, contentLength, done);
//            Toast.makeText(getApplicationContext(),"start",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUIFinish(long bytesWrite, long contentLength, boolean done) {
            super.onUIFinish(bytesWrite, contentLength, done);
//            Toast.makeText(getApplicationContext(),"end",Toast.LENGTH_SHORT).show();
        }
    };
}
