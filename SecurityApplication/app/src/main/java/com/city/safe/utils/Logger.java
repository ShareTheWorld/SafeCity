package com.city.safe.utils;

import android.text.TextUtils;

public final class Logger {

    private static final String TAG = "TAG";
/*///////////////////////////////////////////////////////////////////////////*/
    /**
     * Set true or false if you want read logs or not
     * Set true or false if you want show time/location or not
     */
    private static boolean logEnabled_d = true;
    private static boolean logEnabled_i = true;
    private static boolean logEnabled_e = true;

    private static boolean logEnabled_out_tag = true;
    private static boolean logEnabled_time = false;
    private static boolean logEnabled_location = true;
/*////////////////////////////////////////////////////////////////////////////*/
    /**
     * 记录上一个log的打印的时间
     */
    private static long sLogOutTime;

    static {
        sLogOutTime = System.nanoTime();
    }

    public static String getNanoTime() {
        if (!logEnabled_time)
            return "";
        StringBuffer mid = new StringBuffer((System.nanoTime() - sLogOutTime) + "");
        if (mid.length() > 9)
            mid.insert(mid.length() - 9, ",");
        if (mid.length() > 6)
            mid.insert(mid.length() - 6, ",");
        if (mid.length() > 3)
            mid.insert(mid.length() - 3, ",");
        mid.reverse();
        sLogOutTime = System.nanoTime();
        return mid.toString() + "\t\t";
    }

    public static void d(String tag) {
        if (logEnabled_d) {
            android.util.Log.v(TAG, (logEnabled_out_tag ? tag + " " : "") + getNanoTime() + getLocation());
        }
    }

    public static void d(String tag, String msg) {
        if (logEnabled_d) {
            android.util.Log.v(TAG, (logEnabled_out_tag ? tag + " " : "") + getNanoTime() + getLocation() + msg);
        }
    }

    public static void i(String tag, String msg) {
        if (logEnabled_i) {
            android.util.Log.i(TAG, (logEnabled_out_tag ? tag + " " : "") + getNanoTime() + getLocation() + msg);
        }
    }

    public static void i(String tag) {
        if (logEnabled_i) {
            android.util.Log.i(TAG, (logEnabled_out_tag ? tag + " " : "") + getNanoTime() + getLocation());
        }
    }

    public static void e(String tag, String msg) {
        if (logEnabled_e) {
            android.util.Log.e(TAG, (logEnabled_out_tag ? tag + " " : "") + getNanoTime() + getLocation() + msg);
        }
    }

    public static void e(String tag, String msg, Throwable e) {
        if (logEnabled_e) {
            android.util.Log.e(TAG, (logEnabled_out_tag ? tag + " " : "") + getNanoTime() + getLocation() + msg, e);
        }
    }

    public static void e(String tag, Throwable e) {
        if (logEnabled_e) {
            android.util.Log.e(TAG, (logEnabled_out_tag ? tag + " " : "") + getNanoTime() + getLocation(), e);
        }
    }

    public static void e(String tag) {
        if (logEnabled_e) {
            android.util.Log.e(TAG, (logEnabled_out_tag ? tag + " " : "") + getNanoTime() + getLocation());
        }
    }

    private static String getLocation() {
        if (!logEnabled_location)
            return "";
        final String className = Logger.class.getName();
        final StackTraceElement[] traces = Thread.currentThread()
                .getStackTrace();
        boolean found = false;

        for (StackTraceElement trace : traces) {
            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + ":"
                                + trace.getMethodName() + ":"
                                + trace.getLineNumber() + "]: ";
                    }
                } else if (trace.getClassName().startsWith(className)) {
                    found = true;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }

        return "[]: ";
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                return clazz.getSimpleName();
            }

            return getClassName(clazz.getEnclosingClass());
        }

        return "";
    }

}
