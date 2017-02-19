package com.city.safe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.city.safe.R;
import com.city.safe.bean.FileDataEntity;
import com.city.safe.data.acc.AccSettingActivity;
import com.city.safe.data.investigation.InvestigationActivity;
import com.city.safe.data.spotmonitor.SettingActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SelectDataTypeActivity extends Activity {

    private ListView mListView;
    private int icons[] = new int[]{R.drawable.linearacc_icon,
            R.drawable.coner_icon,
            R.drawable.linearaccandconer_icon,
            R.drawable.displace_icon,
            R.drawable.crack_icon,
            R.drawable.questionnaire_icon,
            R.drawable.image_icon};
    private String texts[];
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        mListView = (ListView) findViewById(R.id.add_data_lv);
        texts = new String[]{getString(R.string.linearacc_data), getString(R.string.coner_data), getString(R.string.linearace_and_coner_data), getString(R.string.distance_data), getString(R.string.crack_data), getString(R.string.questionnaire_data), getString(R.string.image_data)};


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
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.i("hongtao.fu2",position+"");
                Intent intent = null;
                switch (position) {
                    case 0:
                        startAndFinish(AccSettingActivity.class);
                        break;
                    case 1:
//                        startAndFinish(AboutUsActivity.class);
                        Toast.makeText(getApplicationContext(),"有待完善",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
//                        startAndFinish(AboutUsActivity.class);
                        Toast.makeText(getApplicationContext(),"有待完善",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        startAndFinish(SettingActivity.class);
                        break;
                    case 4:
//                        startAndFinish(AboutUsActivity.class);
                        Toast.makeText(getApplicationContext(),"有待完善",Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        chooseQuestion();
                        break;
                    case 6:
                        startAndFinish(SelectPictureActivity.class);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    public void onFinish(View view) {
        Intent intent = new Intent(SelectDataTypeActivity.this, CreatProjectActivity.class);
        startActivity(intent);
        finish();
    }

    public void startAndFinish(Class c) {
        Intent intent = new Intent(SelectDataTypeActivity.this, c);//调用APP
        Log.i("hongtao.fu2",c.toString()+"  "+c.getName());
        intent.putExtra(ProjectDataActivity.PROJECT_ID,getIntent().getStringExtra(ProjectDataActivity.PROJECT_ID));
        startActivity(intent);
        finish();
    }

    public void chooseQuestion() {
        new AlertDialog.Builder(SelectDataTypeActivity.this)
                .setTitle("请选择")
                .setSingleChoiceItems(new String[]{"地震调查", "风灾调查", "洪灾调查"}, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPosition = which;
                            }
                        }
                )
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int type=0;
                        Intent intent = new Intent(SelectDataTypeActivity.this, InvestigationActivity.class);
                        intent.setPackage("com.city.safe");
                        switch (mPosition) {
                            case 0:
                                type= FileDataEntity.TYPE_INV_ONE;
                                break;
                            case 1:
                                type=FileDataEntity.TYPE_INV_TWO;
                                break;
                            case 2:
                                type=FileDataEntity.TYPE_INV_THREE;
                                break;
                        }
                        intent.putExtra(InvestigationActivity.INVESTIGATION_TYPE,type);
                        intent.putExtra(ProjectDataActivity.PROJECT_ID,getIntent().getStringExtra(ProjectDataActivity.PROJECT_ID));
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
