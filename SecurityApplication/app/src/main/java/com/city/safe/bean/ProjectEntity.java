package com.city.safe.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Calendar;

/**
 * Created by user on 16-12-10.
 */

@Table(name="ProjectEntity")
public class ProjectEntity implements IEntity{

    public static final int PROJECT_NEW=0;
    public static final int PROJECT_CREATE_FINISH=1;

    public static final String ID="id";
    public static final String USER_ID="user_id";
    public static final String PROJECT_ID="project_id";
    public static final String NAME="name";
    public static final String DESC="desc";
    public static final String STATE="state";
    public static final String PATHS="paths";
    public static final String TIME="time";
    public static final String LONGITUDE="longitude";
    public static final String LATITUDE="latitude";

    @Column(name = "id",isId = true)
    private int id;
    @Column(name = "project_service_id")
    private String project_service_id;
    @Column(name = "user_id")
    private String user_id;
    @Column(name = "project_id")
    private String project_id;
    @Column(name = "name")
    private String name;
    @Column(name = "desc")
    private String desc;
    @Column(name ="location")
    private String location;
    @Column(name = "state")
    private int state;
    @Column(name ="time")
    private long time;
    public ProjectEntity() {
    }

    public ProjectEntity(String user_id, String project_id, String name, String desc, String location, int state, long time) {
        this.user_id = user_id;
        this.project_id = project_id;
        this.name = name;
        this.desc = desc;
        this.location = location;
        this.state = state;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getProject_service_id() {
        return project_service_id;
    }

    public void setProject_service_id(String project_service_id) {
        this.project_service_id = project_service_id;
    }

    @Override
    public String toString() {
        return "ProjectEntity{" +
                "id=" + id +
                ", project_service_id='" + project_service_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", project_id='" + project_id + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", location='" + location + '\'' +
                ", state=" + state +
                ", time=" + time +
                '}';
    }
}
