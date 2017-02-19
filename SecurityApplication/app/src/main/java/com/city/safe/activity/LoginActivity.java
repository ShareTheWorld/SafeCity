package com.city.safe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.city.safe.R;
import com.city.safe.bean.FileDataEntity;
import com.city.safe.bean.ProjectEntity;
import com.city.safe.bean.UserJson;
import com.city.safe.dao.FileDataDB;
import com.city.safe.dao.ProjectDB;
import com.city.safe.http.PostHttp;
import com.city.safe.utils.SPUtil;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.cookie.DbCookieStore;

import java.io.File;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends Activity {
    public static final int REQUEST_CODE_REGISTER=0;
    EditText login_email;
    EditText login_password;
    Button login;
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        login_email = (EditText)findViewById(R.id.login_emailText);
        login_password = (EditText)findViewById(R.id.login_passwordText);
        login = (Button)findViewById(R.id.login_Button);
        register = (TextView) findViewById(R.id.register_Button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                startActivity(intent);
//                finish();
                final ProgressDialog pd=new ProgressDialog(LoginActivity.this);
                pd.setMessage("登录中...");
                pd.setCancelable(false);
                pd.show();
                String email=login_email.getText().toString();
                final String password=login_password.getText().toString();
                Map<String,String> map=new HashMap<>();
                map.put("email",email.trim()); //email: 123456789@qq.com, password: 123456789
                map.put("password",password);
                PostHttp postHttp=new PostHttp(getApplicationContext());
                postHttp.login(map,new Callback.CommonCallback<String>(){
                    @Override
                    public void onSuccess(String result) {
                        Log.i("hongtao.fu2","onSuccess"+result);
                        DbCookieStore instance = DbCookieStore.INSTANCE;
                        List<HttpCookie> list= instance.getCookies();
                        for(HttpCookie httpCookie:list){
                            Log.i("hongtao.fu2","cookie="+httpCookie);
                        }
//                        String Data = "{students:[{name:'魏祝林',age:25},{name:'阿魏',age:26}],class:'三年二班'}  ";
                        Gson gson = new Gson();
                        UserJson userJson = gson.fromJson(result,UserJson.class);
                        Log.i("hongtao.fu2",userJson.toString());
                        SPUtil.saveValueByKey(LoginActivity.this,SPUtil.USER_ID,userJson.getId());
                        if(userJson.getStatus()==1){//是1 就表示登录ok
                            SPUtil.saveValueByKey(getApplicationContext(),SPUtil.USER_INFO,result);
                            SPUtil.saveValueByKey(getApplicationContext(),SPUtil.USER_PASSWORD,password);
                            Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            LoginActivity.this.startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this,"邮箱或者密码错误",Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Toast.makeText(getApplicationContext(),"登录失败，请检查网络",Toast.LENGTH_SHORT).show();
                        String userInfoJson = SPUtil.getValueByKey(getApplicationContext(), SPUtil.USER_INFO);
                        if(userInfoJson!=null && !"".equals(userInfoJson.trim())){
                            popDialog();
                        }

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {
                        pd.dismiss();
                    }
                });
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent,REQUEST_CODE_REGISTER);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(requestCode==REQUEST_CODE_REGISTER&&resultCode==RESULT_OK && data!=null){
           login_email.setText( data.getStringExtra("email"));
           login_password.setText(data.getStringExtra("password"));
           Log.i("hongtao.fu2","注册成功返回");
       }
    }

    public void popDialog(){
        float scale = this.getResources().getDisplayMetrics().density;
        //计算出一50dp相当与多少px
        int size = (int) (50 * scale + 0.5f);// dp 转 px
        //从资源中加载一张图片到内存里
        Bitmap bitmap = BitmapFactory.decodeResource( this.getResources(), R.drawable.city);
        //将图片压缩/放大为size*size的大小
        bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
        //创建一个Alertilaog的builder，并且在Dialog上面设置提提示的信息和事件的处理代码
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(new BitmapDrawable(bitmap));
        builder.setTitle("无网直接进入");
        builder.setMessage("你所创建的工程和采集的数据都将属于最近登录的用户");
        builder.setPositiveButton("进入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(LoginActivity.this,"无网进入成功",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                LoginActivity.this.startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("重试", null);
        //创建出Dilaog
        AlertDialog dialog = builder.create();
        //将创建出来的Dialog显示在界面上
        dialog.show();
    }
}
