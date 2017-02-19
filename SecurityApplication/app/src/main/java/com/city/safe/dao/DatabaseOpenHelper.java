package com.city.safe.dao;

import org.xutils.DbManager;

/**
 * Created by user on 16-12-10.
 */

public class DatabaseOpenHelper {
    private static final String DB_NAME="city_safe.db";
    private static final int VERSION=1;
    private DbManager.DaoConfig daoConfig;
    public DatabaseOpenHelper() {
        daoConfig = new DbManager.DaoConfig()
                .setDbName(DB_NAME)
                .setDbVersion(1)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        db.getDatabase().enableWriteAheadLogging();
                        //开启WAL, 对写入加速提升巨大(作者原话)
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        //数据库升级操作
                    }
                });
    }
    public DbManager.DaoConfig getDaoConfig(){
        return daoConfig;
    }
}
