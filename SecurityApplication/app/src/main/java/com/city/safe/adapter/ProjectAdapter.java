package com.city.safe.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.city.safe.R;
import com.city.safe.activity.ProjectDataActivity;
import com.city.safe.bean.FileDataEntity;
import com.city.safe.bean.ProjectEntity;
import com.city.safe.dao.FileDataDB;
import com.city.safe.dao.ProjectDB;
import com.city.safe.utils.SPUtil;
import com.city.safe.utils.TimeUtils;

import java.io.File;
import java.util.List;

/**
 * Created by user on 16-12-13.
 */

public class ProjectAdapter extends BaseAdapter{
    private Context mContext;
    private List<ProjectEntity> mDatas;

    public ProjectAdapter(Context mContext,List<ProjectEntity> mDatas) {
        this.mContext=mContext;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        if(mDatas==null) {
            return 0;
        }
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        ProjectEntity project=mDatas.get(position);
        if(view==null){
            LayoutInflater layoutInflater= LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.show_project_item,null);
        }
//        TextView header=(TextView)view.findViewById(R.id.header);
        TextView name=(TextView)view.findViewById(R.id.name);
        TextView desc=(TextView)view.findViewById(R.id.desc);
        TextView state=(TextView)view.findViewById(R.id.state);
        TextView time=(TextView)view.findViewById(R.id.time);
        TextView location=(TextView)view.findViewById(R.id.location);

//        header.setText(String.valueOf(position+1));
        name.setText(project.getName());
        desc.setText(project.getDesc());
        state.setText(""+project.getState());
        time.setText(TimeUtils.secondToTime(String.valueOf(project.getTime())));
        location.setText("("+project.getLocation()+")");

        return view;
    }
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        ProjectEntity project=mDatas.get(position);
        Intent intent=new Intent(mContext, ProjectDataActivity.class);
        intent.putExtra(ProjectDataActivity.PROJECT_ID,project.getProject_id());
        intent.setPackage("com.city.safe");
        SPUtil.saveValueByKey(mContext,SPUtil.CURRENT_PROJECT_ID,project.getProject_id());
        mContext.startActivity(intent);
    }

    public void setmDatas(List<ProjectEntity> mDatas) {
        this.mDatas = mDatas;
        if(mDatas != null) {
            Log.i("hongtao.fu2", mDatas.toString());
        }
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final int p=position;
        Log.i("hongtao.fu2","long click item");
        float scale = mContext.getResources().getDisplayMetrics().density;
        //计算出一50dp相当与多少px
        int size = (int) (50 * scale + 0.5f);// dp 转 px
        //从资源中加载一张图片到内存里
        Bitmap bitmap = BitmapFactory.decodeResource( mContext.getResources(), R.drawable.city);
        //将图片压缩/放大为size*size的大小
        bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
        //创建一个Alertilaog的builder，并且在Dialog上面设置提提示的信息和事件的处理代码
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setIcon(new BitmapDrawable(bitmap));
        builder.setTitle("删除");
        builder.setMessage("你确定要删除此项目");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProjectEntity projectEntity=mDatas.get(p);
                String projectId = projectEntity.getProject_id();
                List<FileDataEntity> list=FileDataDB.getIntance().selectByProjectId(projectId);
                Log.i("hongtao.fu2",projectId);
                Log.i("hongtao.fu2",list.toString());
                for(FileDataEntity l:list){
                    int type = l.getType();
                    if(type!=FileDataEntity.TYPE_IMG){
                        File file=new File(l.getPath());
                        if (file.exists()){
                            file.delete();
                        }
                    }
                    FileDataDB.getIntance().delete(l);
                }
                ProjectDB.getIntance().delete(projectEntity);
                mDatas=ProjectDB.getIntance().select();
                notifyDataSetChanged();

            }
        });
        builder.setNegativeButton("取消", null);
        //创建出Dilaog
        AlertDialog dialog = builder.create();
        //将创建出来的Dialog显示在界面上
        dialog.show();
        return true;
    }
}
