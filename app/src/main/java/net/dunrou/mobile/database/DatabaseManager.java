package net.dunrou.mobile.database;

import android.content.Context;

import net.dunrou.mobile.base.DaoMaster;
import net.dunrou.mobile.base.DaoSession;

/**
 * Created by Stephen on 2018/9/5.
 */

public class DatabaseManager {
    private static final String DATABASE_NAME = "mobile.db";

    private DaoMaster.DevOpenHelper helper;
    private DaoMaster daoMaster;
    private static volatile DatabaseManager databaseManager;


    private DatabaseManager(Context context) {
        helper = new DaoMaster.DevOpenHelper(context.getApplicationContext(), DATABASE_NAME);
        daoMaster = new DaoMaster(helper.getWritableDb());
    }

    public static DatabaseManager getInstance(Context context) {
        if (databaseManager == null){
            synchronized (DatabaseManager.class){
                if (databaseManager == null) {
                    databaseManager = new DatabaseManager(context);
                }
            }
        }
        return databaseManager;
    }

    public DaoMaster.DevOpenHelper getDevOpenHelper() {
        return helper;
    }

    public DaoMaster getDaoMaster() {
        return daoMaster;
    }

    public DaoSession getDaoSession() {
        return daoMaster.newSession();
    }

}
