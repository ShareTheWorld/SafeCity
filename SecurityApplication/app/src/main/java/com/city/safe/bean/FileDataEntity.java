package com.city.safe.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by user on 16-12-10.
 */

@Table(name="FileDataEntity")
public class FileDataEntity implements IEntity{
//    public static final int TYPE_UNKNOW=000;//未知文件类型
//    public static final int TYPE_ACC=100;//加速度文件
//    public static final int TYPE_ANG=200;//角速度文件
//    public static final int TYPE_ACC_AND_ANG=300;//加速度和角速度文件
//    public static final int TYPE_SPOT_ONE =401;//位移数据文件
//    public static final int TYPE_SPOT_TWO =402;//位移数据文件
//    public static final int TYPE_CRACK=500;//裂纹数据文件
//    public static final int TYPE_INV_ONE=600;//调查问卷1 地震
//    public static final int TYPE_INV_TWO=700;//调查问卷2  台风
//    public static final int TYPE_INV_THREE=800;//调查问卷3 洪灾
//    public static final int TYPE_IMG=900;//图片

    public static final int TYPE_UNKNOW=0;//未知文件类型
    public static final int TYPE_ACC=1;//加速度文件
    public static final int TYPE_ANG=2;//角速度文件
    public static final int TYPE_ACC_AND_ANG=3;//加速度和角速度文件
    public static final int TYPE_SPOT_ONE =4;//位移数据文件
    public static final int TYPE_SPOT_TWO =4;//位移数据文件
    public static final int TYPE_CRACK=5;//裂纹数据文件
    public static final int TYPE_INV_ONE=6;//调查问卷1 地震
    public static final int TYPE_INV_TWO=7;//调查问卷2  台风
    public static final int TYPE_INV_THREE=8;//调查问卷3 洪灾
    public static final int TYPE_IMG=9;//图片

    public static final int FILE_STATE_NEW=0;
    public static final int FILE_STATE_FINISH=1;


    public static final String ID="id";
    public static final String PROJECT_ID="project_id";
    public static final String NAME="name";
    public static final String DATA="data";
    public static final String TYPE="type";
    public static final String TIME="time";

    @Column(name = "id",isId = true)
    private long id;//id
    @Column(name = "user_id")
    private String user_id;//项目编号，代表这个文件属于那个user
    @Column(name = "project_id")
    private String project_id;//项目编号，代表这个文件属于那个user
    @Column(name= "file_id")
    private String file_id;
    @Column(name = "name")
    private String name;
    @Column(name = "path")
    private String path;//文件存放的路径
    @Column(name = "type")
    private int type;
    @Column(name = "state")
    private int state;
    @Column(name ="time")
    private long time;

    public FileDataEntity() {
    }

    public FileDataEntity(String user_id, String project_id, String file_id, String name, String path, int type, int state, long time) {
        this.user_id = user_id;
        this.project_id = project_id;
        this.file_id = file_id;
        this.name = name;
        this.path = path;
        this.type = type;
        this.state = state;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "FileDataEntity{" +
                "id=" + id +
                ", user_id='" + user_id + '\'' +
                ", project_id='" + project_id + '\'' +
                ", file_id='" + file_id + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", type=" + type +
                ", state=" + state +
                ", time=" + time +
                '}';
    }
}
