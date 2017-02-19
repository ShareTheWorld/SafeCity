package com.city.safe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import com.city.safe.R;
import com.city.safe.adapter.ProjectAdapter;
import com.city.safe.bean.UserJson;
import com.city.safe.dao.ProjectDB;
import com.city.safe.bean.ProjectEntity;
import com.city.safe.http.PostHttp;
import com.google.gson.Gson;


import org.xutils.common.Callback;
import org.xutils.db.annotation.Column;
import org.xutils.http.cookie.DbCookieStore;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectActivity extends AppCompatActivity {

    ImageView mCreateProjectButton;
    ListView mProjectList;
    ProjectAdapter mProjectAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        mCreateProjectButton = (ImageView) findViewById(R.id.carete_new_project_image_button);
        mProjectList = (ListView)findViewById(R.id.projects);
        ProjectDB projectDB= ProjectDB.getIntance();
        List<ProjectEntity> datas=projectDB.select();

        mProjectAdapter=new ProjectAdapter(this,datas);
        mProjectList.setAdapter(mProjectAdapter);
        mProjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mProjectAdapter.onItemClick(parent,view,position,id);
            }
        });
        mProjectList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return mProjectAdapter.onItemLongClick(parent,view,position,id);
            }
        });

        mCreateProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectActivity.this,CreatProjectActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ProjectDB projectDB= ProjectDB.getIntance();
        List<ProjectEntity> datas=projectDB.select();
        mProjectAdapter.setmDatas(datas);
        mProjectAdapter.notifyDataSetChanged();
    }
}
