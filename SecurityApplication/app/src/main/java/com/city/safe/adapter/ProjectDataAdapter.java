package com.city.safe.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.city.safe.R;
import com.city.safe.bean.FileDataEntity;
import com.city.safe.bean.ProjectEntity;
import com.city.safe.bean.ProjectJson;
import com.city.safe.bean.UploadJson;
import com.city.safe.bean.UserJson;
import com.city.safe.dao.FileDataDB;
import com.city.safe.dao.ProjectDB;
import com.city.safe.data.acc.display.TimeFreqGraphActivity;
import com.city.safe.http.PostHttp;
import com.city.safe.utils.Constants;
import com.city.safe.utils.ImageLoaderConfig;
import com.city.safe.utils.SPUtil;
import com.city.safe.utils.TimeUtils;
import com.city.safe.view.NumberProgressBar;
import com.google.gson.Gson;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoPreviewActivity;
import com.photoselector.util.CommonUtils;

import org.xutils.common.Callback;
import org.xutils.x;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 16-12-14.
 */

public class ProjectDataAdapter extends BaseAdapter implements View.OnClickListener{
    public static final int POSITION=1000;
    public static final int STATE=1001;
    public static final int NEW_STATE=0;
    public static final int UPLOADING=1;
    public static final int UPLOAD_FIAL=2;
    public static final int UPLOAD_SUCCESS=3;

    private Context mContext;
    private List<FileDataEntity> mDatas;
    private ProjectEntity mProjectEntity;
    private UserJson mUserJson;
    private String mProjectId;



    public ProjectDataAdapter(Context mContext,List<FileDataEntity> mDatas,String projectId) {
        this.mContext=mContext;
        this.mDatas = mDatas;
        mProjectEntity= ProjectDB.getIntance().select(projectId);
        String userInfoJson = SPUtil.getValueByKey(mContext, SPUtil.USER_INFO);
        Gson gson = new Gson();
        mUserJson = gson.fromJson(userInfoJson, UserJson.class);
        mProjectId=projectId;
    }
    @Override
    public int getCount() {
        if(mDatas==null){
            return 0;
        }
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        if(mDatas==null){
            return null;
        }
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FileDataEntity project=mDatas.get(position);
        if(convertView==null){
            LayoutInflater layoutInflater= LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.project_data_item,null);
            ViewHolder viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        ViewHolder vh = (ViewHolder) convertView.getTag();
        vh.setData(position);

        return convertView;
    }

    private final class ViewHolder{
        public int mPosition;
        public TextView mId;
        public TextView mUserId;
        public TextView mProjectId;
        public TextView mFileId;
        public TextView mName;
        public TextView mPath;
        public TextView mType;
        public TextView mStatus;
        public TextView mTime;
        public TextView mUpload;
        public TextView mFileSize;
        public ImageView mImage;

        public NumberProgressBar mProgress;

        public ViewHolder(View convertView){
            mId=(TextView)convertView.findViewById(R.id.id);
            mUserId=(TextView)convertView.findViewById(R.id.user_id);
            mProjectId=(TextView)convertView.findViewById(R.id.project_id);
            mFileId=(TextView)convertView.findViewById(R.id.file_id);
            mName=(TextView)convertView.findViewById(R.id.name);
            mPath=(TextView)convertView.findViewById(R.id.path);
            mType=(TextView)convertView.findViewById(R.id.type);
//            mStatus=(TextView)convertView.findViewById(R.id.state);
            mTime=(TextView)convertView.findViewById(R.id.time);
            mFileSize=(TextView)convertView.findViewById(R.id.file_size);
            mImage=(ImageView)convertView.findViewById(R.id.image);
            mUpload=(TextView)convertView.findViewById(R.id.upload);
            mProgress=(NumberProgressBar)convertView.findViewById(R.id.progress);

        }
        public void setData(int position){
            mPosition=position;
            FileDataEntity data=mDatas.get(position);
            mId.setText(String.valueOf(data.getId()));
            mUserId.setText(String.valueOf(data.getUser_id()));
            mProjectId.setText(String.valueOf(data.getProject_id()));
            mFileId.setText(String.valueOf(data.getFile_id()));
            mName.setText(String.valueOf(data.getName()));
            mPath.setText(String.valueOf(data.getPath()));
            mType.setText(String.valueOf(data.getType()));
//            mStatus.setText(data.getState()+"");
            mTime.setText(TimeUtils.secondToTime(String.valueOf(data.getTime())));
            Log.i("hongtao.fu2",TimeUtils.secondToTime(String.valueOf(data.getTime())));
            if(data.getState()==FileDataEntity.FILE_STATE_FINISH){
                mUpload.setBackgroundResource(R.drawable.file_upload_finish);
                mProgress.setProgress(100);
            }else{
                mUpload.setBackgroundResource(R.drawable.upload_ico);
                mProgress.setProgress(0);
            }
            mImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            switch (data.getType()) {
                case FileDataEntity.TYPE_ACC://加速度文件
                    mImage.setImageResource(R.drawable.linearacc_icon);
                    break;
                case FileDataEntity.TYPE_ANG://角速度文件
                    mImage.setImageResource(R.drawable.coner_icon);
                    break;
                case FileDataEntity.TYPE_ACC_AND_ANG://加速度和角速度文件
                    mImage.setImageResource(R.drawable.linearaccandconer_icon);
                    break;
                case FileDataEntity.TYPE_SPOT_ONE://位移数据文件
                    mImage.setImageResource(R.drawable.displace_icon);
                    break;
                case FileDataEntity.TYPE_CRACK://裂纹数据文件
                    mImage.setImageResource(R.drawable.crack_icon);
                    break;
                case FileDataEntity.TYPE_INV_ONE://调查问卷1 地震
                case FileDataEntity.TYPE_INV_TWO://调查问卷2  台风
                case FileDataEntity.TYPE_INV_THREE://调查问卷3 洪灾
                    mImage.setImageResource(R.drawable.questionnaire_icon);
                    break;
                case FileDataEntity.TYPE_IMG://图片
                    x.image().bind(mImage, data.getPath());
                    break;
                default:
                    mImage.setImageResource(R.drawable.city);
                    break;
            }
            long len=new File(data.getPath()).length()/1024;
            String strSize;
            if(len==0){
                strSize=new File(data.getPath()).length()+"B";
            }else{
                strSize=len+"K";
            }
            mFileSize.setText(strSize);
            mUpload.setOnClickListener(ProjectDataAdapter.this);
            mUpload.setTag(this);

        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        FileDataEntity fileDataEntity=mDatas.get(position);
        int type=fileDataEntity.getType();
        String path=fileDataEntity.getPath();
        File file=new File(path);
        if(!file.exists()){
            Toast.makeText(mContext,"文件不存在了",Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(mContext,type+"  "+path,Toast.LENGTH_SHORT).show();
        Log.i("hongtao.fu2","type="+type);
        switch (type){
            case FileDataEntity.TYPE_ACC:
                showAccData(fileDataEntity);
                break;
            case FileDataEntity.TYPE_IMG:
                showPictureData(fileDataEntity);
                break;
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
        builder.setMessage("你确定要删除此项数据");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FileDataDB.getIntance().delete(mDatas.get(p));
                int type = mDatas.get(p).getType();
                if(type!=FileDataEntity.TYPE_IMG){
                    File file=new File(mDatas.get(p).getPath());
                    if (file.exists()){
                        file.delete();
                    }
                }
                mDatas= FileDataDB.getIntance().selectByProjectId(mProjectId);
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
    public void showAccData(FileDataEntity fileDataEntity){
        Intent intent = new Intent();
        intent.setClass(mContext,TimeFreqGraphActivity.class);//
        //用Bundle携带数据
        Bundle bundle=new Bundle();
        bundle.putString("filename",fileDataEntity.getName());
        bundle.putString("filepath",fileDataEntity.getPath());
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }
    public void showPictureData(FileDataEntity fileDataEntity){
        ImageLoaderConfig.initImageLoader(mContext, Constants.BASE_IMAGE_CACHE);
        List<PhotoModel> lists = new ArrayList<PhotoModel>();
        PhotoModel photoModel=new PhotoModel();
        photoModel.setOriginalPath(fileDataEntity.getPath());
        photoModel.setChecked(true);
        lists.add(photoModel);
        Bundle bundle = new Bundle();
        bundle.putSerializable("photos", (Serializable) lists);
        CommonUtils.launchActivity(mContext, PhotoPreviewActivity.class, bundle);
    }


    @Override
    public void onClick(View v) {
        final ViewHolder vh =(ViewHolder)v.getTag();
        vh.mUpload.setOnClickListener(null);
        int position = vh.mPosition;
        int state=mDatas.get(position).getState();
        final FileDataEntity fileDataEntity=mDatas.get(position);
        if(mProjectEntity==null){
            return ;
        }
        if(fileDataEntity.getState()==FileDataEntity.FILE_STATE_FINISH){
            Toast.makeText(mContext,"文件已经上传",Toast.LENGTH_SHORT).show();
            return ;
        }
        if(mProjectEntity.getState()==ProjectEntity.PROJECT_NEW){
            //传件项目
            PostHttp postHttp=new PostHttp(mContext);
            final Map<String,String> map=new HashMap<>();
            map.put("project_id",mProjectEntity.getProject_id());
            map.put("password",SPUtil.getValueByKey(mContext,SPUtil.USER_PASSWORD));
            map.put("user_id",mUserJson.getId());
            map.put("email",mUserJson.getEmail());
            map.put("name",mProjectEntity.getName());
            map.put("desc",mProjectEntity.getDesc());
            map.put("location",mProjectEntity.getLocation());
            map.put("state","0");
            map.put("time",String.valueOf(mProjectEntity.getTime()));
            postHttp.createProject(map,new Callback.CommonCallback<String>(){
                @Override
                public void onSuccess(String result) {
                    Gson gson = new Gson();
                    ProjectJson projectJson = gson.fromJson(result, ProjectJson.class);
                    Log.i("hongtao.fu2",projectJson.toString());
                    if(projectJson!=null&& projectJson.getStatus() == 1){
                        mProjectEntity.setState(ProjectEntity.PROJECT_CREATE_FINISH);
                        mProjectEntity.setProject_service_id(String.valueOf(projectJson.getId()));
                        ProjectDB.getIntance().update(mProjectEntity);
                        Toast.makeText(mContext,"工程创建成功",Toast.LENGTH_SHORT).show();
                        upLoadFileData(fileDataEntity,vh);
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.i("hongtao.fu2","onError="+ex.getMessage());
                    Toast.makeText(mContext,"工程创建失败",Toast.LENGTH_SHORT).show();
                    vh.mUpload.setOnClickListener(ProjectDataAdapter.this);
                    vh.mProgress.setProgress(0);
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    Log.i("hongtao.fu2","onCancelled");
                    vh.mUpload.setOnClickListener(ProjectDataAdapter.this);
                }

                @Override
                public void onFinished() {
                    Log.i("hongtao.fu2","onFinished");
                }
            });
        }else if(mProjectEntity.getState()==ProjectEntity.PROJECT_CREATE_FINISH){
            upLoadFileData(fileDataEntity,vh);
        }
    }
    public void upLoadFileData(FileDataEntity fileDataEntity,final ViewHolder vh){
        PostHttp postHttp=new PostHttp(mContext);
        final FileDataEntity fe=fileDataEntity;
        File file=new File(fileDataEntity.getPath());
        Map<String,String> map=new HashMap<>();
        map.put("id",mProjectEntity.getProject_service_id());
        map.put("user_id",fileDataEntity.getUser_id());
        map.put("project_id",fileDataEntity.getProject_id());
        map.put("file_id",fileDataEntity.getFile_id());
        map.put("email",mUserJson.getEmail());
        map.put("password",SPUtil.getValueByKey(mContext,SPUtil.USER_PASSWORD));
        map.put("name",fileDataEntity.getName());
        map.put("type",String.valueOf(fileDataEntity.getType()));
        map.put("state","0");
        map.put("time",String.valueOf(fileDataEntity.getTime()));
        postHttp.uploadFile(map,new Callback.ProgressCallback<String>(){
            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Log.i("hongtao.fu2","process=  "+(int)((((double)current)/total)*100)+"%");
                vh.mProgress.setProgress((int)((((double)current)/total)*100));
            }

            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                UploadJson uploadJson = gson.fromJson(result, UploadJson.class);
                Log.i("hongtao.fu2","onSuccess=   "+result);
                if(uploadJson.getStatus()==1 && uploadJson.isUpload()){
                    fe.setState(FileDataEntity.FILE_STATE_FINISH);
                    FileDataDB.getIntance().update(fe);
                    notifyDataSetChanged();
                    Toast.makeText(mContext,"文件上传成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext,"服务器没有成功接收文件",Toast.LENGTH_SHORT).show();
                    vh.mProgress.setProgress(0);
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("hongtao.fu2","onError="+ex.getMessage());
                Toast.makeText(mContext,"文件上失败",Toast.LENGTH_SHORT).show();
                vh.mProgress.setProgress(0);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("hongtao.fu2","onCancelled");
                vh.mProgress.setProgress(0);
            }

            @Override
            public void onFinished() {
                Log.i("hongtao.fu2","onFinished");
                vh.mUpload.setOnClickListener(ProjectDataAdapter.this);
            }
        },file);
    }

    public void setmDatas(List<FileDataEntity> mDatas) {
        this.mDatas = mDatas;
    }
}
