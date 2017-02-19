package com.city.safe.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.city.safe.R;
import com.city.safe.adapter.GdAdapter;
import com.city.safe.bean.FileDataEntity;
import com.city.safe.bean.UserJson;
import com.city.safe.dao.FileDataDB;
import com.city.safe.utils.Constants;
import com.city.safe.utils.ImageLoaderConfig;
import com.city.safe.utils.SPUtil;
import com.city.safe.utils.Util;
import com.google.gson.Gson;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoPreviewActivity;
import com.photoselector.ui.PhotoSelectorActivity;
import com.photoselector.util.CommonUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectPictureActivity extends Activity implements OnItemClickListener {
    private GridView gd;
    private GdAdapter adapter;
    private final int SELECT_IMAGE_CODE = 1001;
    // 选择图片
    private Dialog dialog_choose_img_way;
    private String str_choosed_img = "";
    private List<PhotoModel> selected;
    private final int MAX_PHOTOS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageLoaderConfig.initImageLoader(this, Constants.BASE_IMAGE_CACHE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);
        initView();
    }

    private void initView() {
        gd = (GridView) findViewById(R.id.gd);
        selected = restorSelected();
        if (selected == null){
            selected = new ArrayList<PhotoModel>();
            PhotoModel photoModel = new PhotoModel();
            photoModel.setOriginalPath("default");
            selected.add(photoModel);
         }
        adapter = new GdAdapter(SelectPictureActivity.this, selected);
        gd.setAdapter(adapter);
        gd.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Log.i("hongtao.fu","position="+position);
        // TODO Auto-generated method stub
        if (position == selected.size() - 1) {// 如果是最后一个
            showChooseIMG_WAYDialog();
        } else {
            List<PhotoModel> lists = new ArrayList<PhotoModel>();
            lists.addAll(selected);
            lists.remove(lists.size() - 1);
            Bundle bundle = new Bundle();
            bundle.putSerializable("photos", (Serializable) lists);
            CommonUtils.launchActivity(this, PhotoPreviewActivity.class, bundle);
        }
    }

    @SuppressWarnings("unchecked")
    private void enterChoosePhoto() {
        // TODO Auto-generated method stub

        List<PhotoModel> choosed = new ArrayList<PhotoModel>();
        if (selected.size() > 0) {
            choosed.addAll(selected);
            choosed.remove(choosed.size() - 1);
        }
        Intent intent = new Intent(SelectPictureActivity.this,
                PhotoSelectorActivity.class);
        intent.putExtra(PhotoSelectorActivity.KEY_MAX, MAX_PHOTOS);
        Bundle bundle = new Bundle();
        Object o=choosed;
        bundle.putParcelableArrayList("selected", (ArrayList<? extends Parcelable>) o);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, SELECT_IMAGE_CODE);
    }


    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        AseoZdpAseo.initFinalTimer(this);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        startActivity(intent);
        super.onBackPressed();
        saveSelected();
    }
    private void saveSelected(){
        String s="";
        for(PhotoModel pm:selected){
            s+=pm.getOriginalPath()+",,,"+pm.isChecked()+"---";
        }
        SPUtil.saveValueByKey(getApplicationContext(),SPUtil.SELECTED_IMG,s);

    }
    private void cleanAllPicture(){
        SPUtil.saveValueByKey(getApplicationContext(),SPUtil.SELECTED_IMG,"");
    }
    private List<PhotoModel> restorSelected(){
        List<PhotoModel> list=new ArrayList<PhotoModel>();
        String selectedImgStr=SPUtil.getValueByKey(getApplicationContext(),SPUtil.SELECTED_IMG);
        if(selectedImgStr==null||"".equals(selectedImgStr)){
            return null;
        }
        String mid[]=selectedImgStr.split("---");
        for(String s:mid){
            if(s!=null&&!"".equals(s)){
                String m[]=s.split(",,,");
                boolean b=false;
                if("true".equals(m[1])){
                    b=true;
                }
                PhotoModel pm=new PhotoModel(m[0],b);
                list.add(pm);
            }
        }
        return list;
    }

    /*
     * 选择图片上传的方式
     */
    private void showChooseIMG_WAYDialog() {
        dialog_choose_img_way = new Dialog(this, R.style.MyDialogStyle);
        dialog_choose_img_way.setContentView(R.layout.dialog_choose_img);
        dialog_choose_img_way.setCanceledOnTouchOutside(true);
        dialog_choose_img_way.findViewById(R.id.other_view).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog_choose_img_way.cancel();
                    }
                });
        dialog_choose_img_way.findViewById(R.id.dialog_cancel)
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog_choose_img_way.cancel();
                    }
                });
        // 拍照上传
        dialog_choose_img_way.findViewById(R.id.choose_by_camera)
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog_choose_img_way.cancel();
                        if (selected.size() > MAX_PHOTOS) {
                            Toast.makeText(SelectPictureActivity.this,
                                    "最多上传" + MAX_PHOTOS + "张",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Util.selectPicFromCamera(SelectPictureActivity.this);
                        }
                    }
                });
        // 本地上传
        dialog_choose_img_way.findViewById(R.id.choose_by_local)
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog_choose_img_way.cancel();
                        enterChoosePhoto();

                    }
                });
        dialog_choose_img_way.show();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_IMAGE_CODE:
                    List<PhotoModel> photos = (List<PhotoModel>) data.getExtras()
                            .getSerializable("photos");
                    selected.clear();
                    adapter.notifyDataSetChanged();
                    selected.addAll(photos);
                    PhotoModel addModel = new PhotoModel();
                    addModel.setOriginalPath("default");
                    selected.add(addModel);
                    adapter.notifyDataSetChanged();
                    break;

                case Util.CAMERA_PHOTO:// 拍照上传
                    if (Util.cameraFile != null && Util.cameraFile.exists()) {
                        str_choosed_img = Util.cameraFile.getAbsolutePath();
                        PhotoModel cameraPhotoModel = new PhotoModel();
                        cameraPhotoModel.setChecked(true);
                        cameraPhotoModel.setOriginalPath(str_choosed_img);
                        if (selected.size() > 0) {// 如果原来有图片
                            selected.remove(selected.size() - 1);
                        }
                        selected.add(cameraPhotoModel);
                        PhotoModel addModel1 = new PhotoModel();
                        addModel1.setChecked(false);
                        addModel1.setOriginalPath("default");
                        selected.add(addModel1);
                        adapter.notifyDataSetChanged();
                        MediaScannerConnection.scanFile(SelectPictureActivity.this,
                                new String[]{str_choosed_img}, null, null);
                    } else {
                        Util.showToast(SelectPictureActivity.this, "获取照片失败，请重试");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void addPicture(View view) {
        cleanAllPicture();
        if (selected != null && selected.size() >= 1) {
            for (int i = 0; i < selected.size() - 1; i++) {
                PhotoModel pm = selected.get(i);
                String path = pm.getOriginalPath();
                File file = new File(path);
                String userInfoJson = SPUtil.getValueByKey(getApplicationContext(), SPUtil.USER_INFO);
                Gson gson = new Gson();
                UserJson user = gson.fromJson(userInfoJson, UserJson.class);
                Log.i("hongtao.fu2", userInfoJson);
                long time=System.currentTimeMillis();
                FileDataEntity fileDataEntity = new FileDataEntity
                        (user.getId(),
                                getIntent().getStringExtra(ProjectDataActivity.PROJECT_ID),
                                "FILE_ID_" + Long.toHexString(time).toUpperCase(),//会在copyFileToProject的时候修改
                                file.getName(),//会在copyFileToProject的时候修改
                                path,//会在copyFileToProject的时候修改
                                FileDataEntity.TYPE_IMG, 0,
                                time//会在copyFileToProject的时候修改
                        );
                FileDataDB.getIntance().insert(fileDataEntity);
            }
        }
        Toast.makeText(SelectPictureActivity.this, "图片采集成功", Toast.LENGTH_SHORT).show();
        Log.i("hongtao.fu2", "copy finish");
        finish();


    }
    public void canclePicture(View view){
        this.finish();
    }
   /* public void addPicture(View view){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("图片存储中...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        cleanAllPicture();
        new Thread(){
            @Override
            public void run() {
                if(selected!=null&&selected.size()>=1) {
                    for (int i = 0; i < selected.size() - 1; i++) {
                        PhotoModel pm = selected.get(i);
                        String path = pm.getOriginalPath();
                        File file=new File(path);
                        String userInfoJson=SPUtil.getValueByKey(getApplicationContext(),SPUtil.USER_INFO);
                        Gson gson=new Gson();
                        UserJson user= gson.fromJson(userInfoJson, UserJson.class);
                        Log.i("hongtao.fu2",userInfoJson);
                        FileDataEntity fileDataEntity = new FileDataEntity
                                (user.getId(),
                                        getIntent().getStringExtra(ProjectDataActivity.PROJECT_ID),
                                        "FILE_ID_" + Long.toHexString(System.currentTimeMillis()).toUpperCase(),//会在copyFileToProject的时候修改
                                        file.getName(),//会在copyFileToProject的时候修改
                                        path,//会在copyFileToProject的时候修改
                                        FileDataEntity.TYPE_IMG, 0,
                                        System.currentTimeMillis()//会在copyFileToProject的时候修改
                                );
                        FileUtils.copyFileToProject(SelectPictureActivity.this,path,fileDataEntity);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(SelectPictureActivity.this,"图片采集成功",Toast.LENGTH_SHORT).show();
                            Log.i("hongtao.fu2","copy finish");
                            finish();
                        }
                    });
                }
            }
        }.start();

    }*/
}
