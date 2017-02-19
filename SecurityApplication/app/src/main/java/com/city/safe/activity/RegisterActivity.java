package com.city.safe.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.city.safe.R;
import com.city.safe.bean.RegisterStatusJson;
import com.city.safe.http.PostHttp;
import com.city.safe.utils.RegexValidateUtil;
import com.google.gson.Gson;

import org.xutils.common.Callback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.internal.tls.OkHostnameVerifier;


public class RegisterActivity extends AppCompatActivity {

    EditText register_email;
    EditText register_password;
    EditText register_password2;
    EditText register_username;
    TextView serviceword;
    Button cancle;
    Button agree;
    CheckBox register_checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_email = (EditText)findViewById(R.id.register_email);
        register_password = (EditText)findViewById(R.id.register_password);
        register_password2 = (EditText)findViewById(R.id.register_password2);
        register_username = (EditText)findViewById(R.id.register_username);
        serviceword = (TextView) findViewById(R.id.serviceword);
        cancle = (Button)findViewById(R.id.cancle);
        agree = (Button)findViewById(R.id.agree);
        register_checkbox = (CheckBox)findViewById(R.id.regisster_checkbox);
        register_checkbox.setChecked(false);



        serviceword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout servicewordd = (LinearLayout)getLayoutInflater()
                        .inflate(R.layout.serviceword,null);
                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle(getString(R.string.serviceword))
                        .setView(servicewordd)
                        .setPositiveButton(
                                getString(R.string.ok_button),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=register_email.getText().toString();
                String p1=register_password.getText().toString();
                String p2=register_password2.getText().toString();
                String name=register_username.getText().toString();
                if(!RegexValidateUtil.checkEmail(email)){
                    Toast.makeText(getApplicationContext(),"邮箱格式不正确",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(p1==null || p1.length()<6 || !p1.equals(p2)){
                    Toast.makeText(getApplicationContext(),"密码不一致或小于6位",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(name==null||name.length()<3){
                    Toast.makeText(RegisterActivity.this, "用户名必须大于三位", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!register_checkbox.isChecked()){
                    Toast.makeText(RegisterActivity.this, R.string.please_agree, Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String,String> map=new HashMap<>();
                map.put("email",email.trim()); //email: 123456789@qq.com, password: 123456789
                map.put("password",p1);
                map.put("name",name);
                register(map);

            }
        });
    }
//    public void register(String )
    public void register(final Map<String,String> map){
        PostHttp postHttp=new PostHttp(getApplicationContext());
        postHttp.register(map,new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("hongtao.fu2","result="+result);
                Gson gson=new Gson();
                RegisterStatusJson registerStatusJson=gson.fromJson(result, RegisterStatusJson.class);
                if(registerStatusJson.getStatus()==1){
                    Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_SHORT).show();
                    Intent intent=getIntent();
                    Bundle data = new Bundle();
                    data.putString("email",map.get("email"));
                    data.putString("password",map.get("password"));
                    intent.putExtras(data);
                    setResult(RESULT_OK,intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"注册失败，可能你的邮箱已经注册过",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                setResult(RESULT_CANCELED);
                Toast.makeText(getApplicationContext(),"注册失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
