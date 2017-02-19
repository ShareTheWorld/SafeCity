package com.city.safe.dao;

import com.city.safe.bean.FileDataEntity;
import com.city.safe.bean.ProjectEntity;
import com.city.safe.utils.Logger;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

/**
 * Created by user on 16-12-10.
 */
/**
 * 　┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　　┃
 * ┃　　　━　　　┃
 * ┃　┳┛　┗┳　┃
 * ┃　　　　　　　┃
 * ┃　　　┻　　　┃
 * ┃　　　　　　　┃
 * ┗━┓　　　┏━┛
 * 　　┃　　　┃神兽保佑
 * 　　┃　　　┃代码无BUG！
 * 　　┃　　　┗━━━┓
 * 　　┃　　　　　　　┣┓
 * 　　┃　　　　　　　┏┛
 * 　　┗┓┓┏━┳┓┏┛
 * 　　　┃┫┫　┃┫┫
 * 　　　┗┻┛　┗┻┛
 */

public class FileDataDB implements IEntityDb<FileDataEntity>{
    private static final String TAG="FileDataDB";
    private DbManager db;
    private static FileDataDB mFileDataDB;
    private FileDataDB(){
        DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper();
        db = x.getDb(databaseOpenHelper.getDaoConfig());
    }
    //构造方法私有化,拿到DbManager对象
    public  static FileDataDB getIntance(){
        if (mFileDataDB == null){
            synchronized (ProjectDB.class){
                if(mFileDataDB==null){
                    mFileDataDB = new FileDataDB();
                }
            }
        }
        return mFileDataDB;
    }

    @Override
    public boolean insert(FileDataEntity entity) {
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
    public boolean delete(FileDataEntity entity) {
        if(entity!=null){
            try{
                db.delete(entity);
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
    public boolean update(FileDataEntity entity) {
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
    public List<FileDataEntity> select(String[] id) {
        return null;
    }

    @Override
    public List<FileDataEntity> selectByKey(String key) {
        return null;
    }

    @Override
    public FileDataEntity select(String id) {
        return null;
    }

    public List<FileDataEntity> selectByProjectId(String id) {
        List<FileDataEntity> list=null;
        try {
            list= db.selector(FileDataEntity.class).where(FileDataEntity.PROJECT_ID,"=",id).orderBy(FileDataEntity.TIME,true).findAll();//.findAll();
        }catch(DbException e){
            Logger.e(TAG,"select project entity error");
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<FileDataEntity> select() {
        List<FileDataEntity> list=null;
        try {
            list= db.findAll(FileDataEntity.class);
        }catch(DbException e){
            Logger.e(TAG,"select project entity error");
            e.printStackTrace();
        }
        return list;
    }
}
