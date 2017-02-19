package com.city.safe.bean;

/**
 * Created by user on 16-12-27.
 */

public class UserJson {

    /**
     * status : 1
     * url : /web_forMrzhao/index.php/Index/Index/index.html
     * id : 100
     * email : 123456789@qq.com
     * name : user
     * photo : 123456789@qq.com.jpg
     * point : 0
     * age : 20
     * sex : 20
     */

    private int status;
    private String url;
    private String id;
    private String email;
    private String name;
    private String photo;
    private String point;
    private String age;
    private String sex;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "UserJson{" +
                "status=" + status +
                ", url='" + url + '\'' +
                ", id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", point='" + point + '\'' +
                ", age='" + age + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}
