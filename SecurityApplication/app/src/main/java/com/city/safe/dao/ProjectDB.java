package com.city.safe.dao;

import android.util.Log;

import com.city.safe.activity.LoginActivity;
import com.city.safe.bean.FileDataEntity;
import com.city.safe.bean.ProjectEntity;
import com.city.safe.utils.Logger;
import com.city.safe.utils.SPUtil;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

/**
 * Created by user on 16-12-10.
 */

public class ProjectDB implements IEntityDb<ProjectEntity> {
    private static final String TAG="ProjectDB";
    private DbManager db;
    private static ProjectDB mProjectDB;
    private ProjectDB(){
        DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper();
        db = x.getDb(databaseOpenHelper.getDaoConfig());
    }
    //构造方法私有化,拿到DbManager对象
    public  static ProjectDB getIntance(){
        if (mProjectDB == null){
            synchronized (ProjectDB.class){
                if(mProjectDB==null){
                    mProjectDB = new ProjectDB();
                }
            }
        }
        return mProjectDB;
    }
    //获取PersonDB实例
    @Override
    public boolean insert(ProjectEntity entity) {
        if (entity == null){
            Logger.e(TAG,"entity is null");
            Exception e = new Exception("entity is null");
            e.printStackTrace();
            return false;
        }
        try {
            db.save(entity);
        } catch (DbException e) {
            Logger.e(TAG,"save project error");
            e.printStackTrace();
            return false;
        }
        Logger.i(TAG,"insert data   "+entity);
        return true;
    }

    @Override
    public boolean delete(String[] id) {
        return false;
    }

    public boolean delete(ProjectEntity entity) {
        if(entity!=null){
            try{
                db.delete(entity);
            }catch (DbException e){
                Logger.e(TAG,"delete entity is error");
                e.printStackTrace();
            }
        }else{
            Logger.i(TAG,"entitys is null");
        }
        return false;
    }

    @Override
    public boolean update(ProjectEntity entity ) {
        if(entity!=null){
            try{
                db.saveOrUpdate(entity);
            }catch (DbException e){
                Logger.e(TAG,"update entity is error");
                e.printStackTrace();
            }
        }else{
            Logger.i(TAG,"entitys is null");
        }
        return false;
    }

    @Override
    public List<ProjectEntity> select(String[] id) {
        return null;
    }

    @Override
    public ProjectEntity select(String projectId) {
        List<ProjectEntity> list=null;
        try {
            list=db.selector(ProjectEntity.class).where(ProjectEntity.PROJECT_ID,"=",projectId).orderBy(ProjectEntity.TIME,true).findAll();

        }catch(DbException e){
            Logger.e(TAG,"select project entity error");
            e.printStackTrace();
        }
        if(list!=null&&list.size()>=1){
            return list.get(0);
        }
        return null;
    }
    @Override
    public List<ProjectEntity> select() {
        List<ProjectEntity> list=null;
        try {
            //list= db.findAll(ProjectEntity.class);
            String userId= SPUtil.getValueByKey(x.app(),SPUtil.USER_ID);
            Log.i("hongtao.fu2","user id ="+userId);
            list=db.selector(ProjectEntity.class).where(ProjectEntity.USER_ID,"=",userId).orderBy(ProjectEntity.TIME,true).findAll();

        }catch(DbException e){
            Logger.e(TAG,"select project entity error");
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ProjectEntity> selectByKey(String key) {
        return null;
    }
}
