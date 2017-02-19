package com.city.safe.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.city.safe.bean.FileDataEntity;
import com.city.safe.dao.FileDataDB;

import org.xutils.common.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by user on 16-12-31.
 */

public class FileUtils {
    public static boolean copyFileToProject(Context context, String source, FileDataEntity fileDataEntity){
        File s=new File(source);
        if(!s.exists()){
            return false;
        }
        if(fileDataEntity==null){
            return false;
        }
        Long time=System.currentTimeMillis();

        String path= Environment.getExternalStorageDirectory().getPath()+"/citysafe/";
        File file=new File(path);
        if(!file.exists()) {
            file.mkdirs();
        }
        String target=path+Long.toHexString(time).toUpperCase()+"_"+s.getName();
        boolean result=org.xutils.common.util.FileUtil.copy(source,target);
        fileDataEntity.setFile_id("FILE_ID_"+Long.toHexString(time).toUpperCase());
        fileDataEntity.setTime(time);
        fileDataEntity.setName(Long.toHexString(time).toUpperCase()+"_"+s.getName());
        fileDataEntity.setPath(target);
        if(result){
            FileDataDB.getIntance().insert(fileDataEntity);
        }
        Log.i("hongtao.fu2","result="+result);
        return result;
    }

    public static String saveFileToProject(Context context,String content,String name){
        if(context==null || content==null || name==null || "".equals(name.trim())){
            return "";
        }
        Long time=System.currentTimeMillis();
        String path= Environment.getExternalStorageDirectory().getPath()+"/citysafe/";
        File file=new File(path);
        if(!file.exists()) {
            file.mkdirs();
        }

        File f=new File(path+name);
        OutputStream outputStream=null;
        try {
            outputStream= new FileOutputStream(f);
            outputStream.write(content.getBytes());
        }catch (FileNotFoundException e){
            Log.i("FileUtils",f.getPath()+" not found");
            return "";
        }catch(IOException io){
            Log.i("FileUtils",f.getPath()+" write exception");
            return "";
        }finally {
            if(outputStream!=null){
                try {
                    outputStream.close();
                }catch (IOException io){
                    Log.i("FileUtils",f.getPath()+" close file fail");
                    return "";
                }
            }
        }
        return path+name;
    }
}
