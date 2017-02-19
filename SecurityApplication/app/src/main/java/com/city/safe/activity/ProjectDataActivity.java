package com.city.safe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.city.safe.R;
import com.city.safe.adapter.ProjectDataAdapter;
import com.city.safe.bean.FileDataEntity;
import com.city.safe.dao.FileDataDB;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;


public class ProjectDataActivity extends AppCompatActivity {
    public static final String PROJECT_ID ="project_id";

    @ViewInject(R.id.listview)
    private ListView mListView;
    ProjectDataAdapter mProjectDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_project_data);
        x.view().inject(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mProjectDataAdapter.onItemClick(parent,view,position,id);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return mProjectDataAdapter.onItemLongClick(parent,view,position,id);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent=getIntent();
        String projectId=intent.getStringExtra(PROJECT_ID);
        FileDataDB fileDataDb=FileDataDB.getIntance();
        List<FileDataEntity> data=fileDataDb.selectByProjectId(projectId);
        Log.i("hongtao.fu2","data="+data);
        if(mProjectDataAdapter==null){
            mProjectDataAdapter=new ProjectDataAdapter(this,data,projectId);
            mListView.setAdapter(mProjectDataAdapter);
        }else{
            mProjectDataAdapter.setmDatas(data);
            mProjectDataAdapter.notifyDataSetChanged();
        }


    }

    public void addNewData(View view){
        Intent intent=new Intent(this,SelectDataTypeActivity.class);
        intent.setPackage("com.city.safe");
        intent.putExtra(PROJECT_ID,getIntent().getStringExtra(PROJECT_ID));
        startActivity(intent);
    }
}
