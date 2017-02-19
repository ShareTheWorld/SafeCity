package com.city.safe.utils;

/**
 * Created by user on 16-12-13.
 */
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by luenghw on 2016/4/26.
 */

public class TimeUtils {

    /**
     * 传递进来时间戳秒，返回天数，大于14天则显示具体发帖时间
     *
     * @param milliSecond：1409900705000
     * @return 10天前
     */
    public static String secondToTime(String milliSecond) {
        if (isBlank(milliSecond))
            return "";
        Date now = new Date();
        BigDecimal db = new BigDecimal(milliSecond);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String data = format.format(db.longValue() * 1000);      //根据时间戳毫秒获取格式为yyyy-MM-dd的时间
        //modiy by hongtao.fu remove 8*60*60这个是为了处理时区问题 begin
        //long between = (now.getTime() / 1000 - db.longValue() / 1000 + 8 * 60 * 60);  //8*60*60这个是为了处理时区问题
        long between = (now.getTime() / 1000 - db.longValue() / 1000/* + 8 * 60 * 60*/);  //8*60*60这个是为了处理时区问题
        //modiy by hongtao.fu remove 8*60*60这个是为了处理时区问题 end
        long day = between / (24 * 3600);

        long hour = between % (24 * 3600) / 3600;

        long minute = between % 3600 / 60;

        long second = between % 60;

        String result = "刚刚";

        if (day > 14) {
            result = data;
        } else if (day <= 14 && day > 0) {
            result = String.valueOf(day) + "天前";
        } else if (hour > 0) {
            result = String.valueOf(hour) + "小时前";
        } else if (minute > 0) {
            result = String.valueOf(minute) + "分钟前";
        } else if (second > 0) {
            result = "刚刚";
        }

        return result;
    }

    /**
     * @param dateFormat format: 2016-04-24T10:00:10+08:00
     * @return 返回天数，大于14天则显示具体发帖时间
     */
    public static String progressDate(String dateFormat) {
        // releaseDate format: 2016-04-24T10:00:10+08:00
        String dateStr = "";
        if (isBlank(dateFormat) || dateFormat.length() < 19)
            return "";
        if (dateFormat.indexOf("+") == -1) {
            dateStr = dateFormat;
        } else {
            dateStr = dateFormat.substring(0, dateFormat.indexOf("+"));
        }

        Date date = new Date();
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            date = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            return "";
        }
        long between = (now.getTime() - date.getTime()) / 1000; // 2个时间相差多少秒

        long day = between / (24 * 3600);

        long hour = between % (24 * 3600) / 3600;

        long minute = between % 3600 / 60;

        long second = between % 60;

        String result = "";


        if (day > 14) {
            result = dateFormat.substring(0, dateFormat.indexOf("T"));
        } else if (day <= 14 && day > 0) {
            result = String.valueOf(day) + "天前";
        } else if (hour > 0) {
            result = String.valueOf(hour) + "小时前";
        } else if (minute > 0) {
            result = String.valueOf(minute) + "分钟前";
        } else {
            result = "1分钟前";
        }

        return result;
    }

    public static boolean isBlank(String str) {
        if (str == null || str.trim().length() == 0) {
            return true;
        }
        return false;
    }
}