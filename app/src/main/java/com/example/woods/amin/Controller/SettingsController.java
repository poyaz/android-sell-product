package com.example.woods.amin.Controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.woods.amin.Database.DaoMaster;
import com.example.woods.amin.Database.DaoSession;
import com.example.woods.amin.Database.Settings;
import com.example.woods.amin.Database.SettingsDao;
import com.example.woods.amin.config;

import java.util.List;

public class SettingsController {
    private SettingsDao settingsDao;
    private Context context;

    public SettingsController(Context context) {
        this.context = context;
        this.settingsDao = setupDb();
    }

    public List<Settings> getSettingsList() {
        return this.settingsDao.queryBuilder().build().list();
    }

    public void addAdminLogin(String username, String password) {
        this.settingsDao.insertInTx(
                new Settings("username", username),
                new Settings("password1", password),
                new Settings("password2", "123456")
        );
    }

    public String getAdminLoginInformation() {
        List<Settings> settingsList = this.settingsDao.queryBuilder().where(
                SettingsDao.Properties.Key.in("username", "password1")
        ).orderDesc(SettingsDao.Properties.Key).build().list();

        if (settingsList.size() == 2) {
            return settingsList.get(0).getValue() + ":" + settingsList.get(1).getValue();
        } else {
            return "";
        }
    }

    public void updateValueOfKey(String key, String value) {
        Log.v("update", "updated!!!!!!!!!!!!!!!");
        this.settingsDao.queryBuilder().where(SettingsDao.Properties.Key.eq(key))
                .buildDelete().executeDeleteWithoutDetachingEntities();

        this.settingsDao.insert(new Settings(key, value));
    }

    public Boolean checkTwoAuthenticate(String password) {
        return this.settingsDao.queryBuilder()
                .where(SettingsDao.Properties.Key.eq("password2"), SettingsDao.Properties.Value.eq(password))
                .buildCount().count() > 0;
    }

    private SettingsDao setupDb() {
        DaoMaster.DevOpenHelper masterHelper = new DaoMaster.DevOpenHelper(this.context, config.DB_NAME, null);
        SQLiteDatabase db = masterHelper.getWritableDatabase();
        DaoMaster master = new DaoMaster(db);
        DaoSession masterSession = master.newSession();

        return masterSession.getSettingsDao();
    }
}
