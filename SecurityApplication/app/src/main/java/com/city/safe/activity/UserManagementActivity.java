package com.city.safe.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.city.safe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserManagementActivity extends AppCompatActivity {
    public static final String SHARE_LINK="http://115.28.12.16/download/SafeCity.apk";


    private ListView mListView;
    private int icons[]=new int[]{R.drawable.mysign_icon,R.drawable.mlevel_icon,R.drawable.mydata_icon,R.drawable.mshare_icon,R.drawable.mcommunication_icon};
    private String texts[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        mListView = (ListView) findViewById(R.id.user_management_lv);
        texts=new String[]{getString(R.string.mysign),getString(R.string.mlevel),getString(R.string.mydata),getString(R.string.mshare),getString(R.string.mcommunication)};


        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < icons.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("icon", icons[i]);
            map.put("text", texts[i]);
            data.add(map);
        }
        String[] from = new String[]{"icon", "text"};
        int[] to = new int[]{R.id.acc_iv, R.id.text_tv};
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.add_data_item, from, to);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch (position) {
                    case 0:
//                        intent = new Intent(UserManagementActivity.this, AboutUsActivity.class);//有待完善
//                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"有待完善",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
//                        intent = new Intent(UserManagementActivity.this, AboutUsActivity.class);//有待完善
//                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"有待完善",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        intent = new Intent(UserManagementActivity.this, MyDataActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain"); //纯文本
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                        intent.putExtra(Intent.EXTRA_TEXT, "城市安全\n\n"+SHARE_LINK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Intent.createChooser(intent, "推荐给好友"));
                        break;
                    case 4:

                        Uri uri = Uri.parse("mailto:wnnian@163.com");
                        String[] email ={"wnnian@163.com"};
                        intent  = new Intent(Intent.ACTION_SENDTO,uri);
                        startActivity(Intent.createChooser(intent,getString(R.string.choseemail)));
                        break;

                    default:
                        break;
                }


            }
        });
    }





    public void onFinish(View view){
        Intent intent = new Intent(UserManagementActivity.this, CreatProjectActivity.class);
        startActivity(intent);
        finish();
    }
}
