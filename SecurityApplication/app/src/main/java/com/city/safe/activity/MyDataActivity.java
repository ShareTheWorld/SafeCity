package com.city.safe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.city.safe.R;
import com.city.safe.bean.RegisterStatusJson;
import com.city.safe.bean.UserJson;
import com.city.safe.http.PostHttp;
import com.city.safe.utils.RegexValidateUtil;
import com.city.safe.utils.SPUtil;
import com.google.gson.Gson;

import org.xutils.common.Callback;

import java.util.HashMap;
import java.util.Map;

public class MyDataActivity extends AppCompatActivity {

    TextView mydatda_email;
    EditText mydatda_password;
    EditText mydatda_password2;
    EditText mydatda_password3;
    TextView mydatda_username;
    Button cancle;
    Button agree;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_data);
        mydatda_email = (TextView) findViewById(R.id.register_email);
        mydatda_password = (EditText)findViewById(R.id.register_password);
        mydatda_password2 = (EditText)findViewById(R.id.register_password2);
        mydatda_password3 = (EditText)findViewById(R.id.register_password3);
        mydatda_username = (TextView) findViewById(R.id.register_username);
        cancle = (Button)findViewById(R.id.cancle);
        agree = (Button)findViewById(R.id.agree);


        String userInfoJson = SPUtil.getValueByKey(getApplicationContext(), SPUtil.USER_INFO);
        Gson gson = new Gson();
        UserJson user = gson.fromJson(userInfoJson, UserJson.class);

        mydatda_email.setText(user.getEmail());
        mydatda_username.setText(user.getName());


        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mydatda_email.getText().toString().trim();
                String p=mydatda_password.getText().toString().trim();
                String p2=mydatda_password2.getText().toString().trim();
                String p3=mydatda_password3.getText().toString().trim();

                if(!RegexValidateUtil.checkEmail(email)){
                    Toast.makeText(getApplicationContext(),"邮箱格式不正确",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(p==null||"".equals(p)){
                    Toast.makeText(getApplicationContext(), "请输入旧密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(p2==null || p2.length()<6 || !p2.equals(p3)){
                    Toast.makeText(getApplicationContext(),"密码不一致或小于6位",Toast.LENGTH_SHORT).show();
                    return;
                }
                PostHttp ph=new PostHttp(getApplicationContext());
                Map<String,String> map=new HashMap<>();
                map.put("email",email.trim()); //email: 123456789@qq.com, password: 123456789
                map.put("password",p);
                map.put("newPassword",p2);
                modifyInfo(map);
            }
        });
    }
    public void modifyInfo(final Map<String,String> map){
        PostHttp postHttp=new PostHttp(getApplicationContext());
        postHttp.modifyInfo(map,new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("hongtao.fu","result="+result);
                Gson gson=new Gson();
                RegisterStatusJson registerStatusJson=gson.fromJson(result, RegisterStatusJson.class);
                if(registerStatusJson.getStatus()==1){
                    Toast.makeText(getApplicationContext(),"密码修改成功",Toast.LENGTH_SHORT).show();
                    Intent intent=getIntent();
                    Bundle data = new Bundle();
                    data.putString("email",map.get("email"));
                    data.putString("password",map.get("newPassword"));
                    intent.putExtras(data);
                    setResult(RESULT_OK,intent);
                    SPUtil.saveValueByKey(getApplicationContext(),SPUtil.USER_PASSWORD,map.get("newPassword"));
                    finish();
                Toast.makeText(MyDataActivity.this, R.string.changesuccessful, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"密码修改失败",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                setResult(RESULT_CANCELED);
                Toast.makeText(getApplicationContext(),"修改失败",Toast.LENGTH_SHORT).show();
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
