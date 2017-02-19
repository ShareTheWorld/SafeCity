package com.city.safe.bean;

/**
 * Created by user on 16-12-10.
 */

public class UserEntity {
    private String name;
    private String id;
    private String email;
    private String password;
    private int mark;

    public UserEntity() {
    }

    public UserEntity(String id, String name, String email, String password, int mark) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.mark = mark;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getMark() {
        return mark;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", mark=" + mark +
                '}';
    }
}
