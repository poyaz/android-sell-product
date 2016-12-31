package com.example.woods.amin.Controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.woods.amin.Database.DaoMaster;
import com.example.woods.amin.Database.DaoSession;
import com.example.woods.amin.Database.Users;
import com.example.woods.amin.Database.UsersDao;
import com.example.woods.amin.config;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Date;
import java.util.List;

public class UsersController {
    private UsersDao usersDao;
    private Context context;
    private Long id = -1L;

    public UsersController(Context context) {
        this.context = context;
        this.usersDao = setupDb();
    }

    public void addNewUser(Bundle data) {
        Date date = new Date();

        Users users = new Users(
                null,
                data.getString("name"),
                data.getString("address"),
                data.getString("phone"),
                data.getString("mobile"),
                date
        );
        this.id = this.usersDao.insert(users);
    }

    public Long getId() {
        return this.id;
    }

    public List<Users> getUserList() {
        return _getUserList("", "");
    }

    public List<Users> getUserList(String whereOrOrderBy) {
        if(whereOrOrderBy.equalsIgnoreCase("%new-user%")) {
            return _getUserList("", whereOrOrderBy);
        } else {
            return _getUserList(whereOrOrderBy, "");
        }
    }

    public List<Users> getUserList(String where, String orderBy) {
        return _getUserList(where, orderBy);
    }

    private List<Users> _getUserList(String where, String orderBy) {
        QueryBuilder<Users> usersQueryBuilder = this.usersDao.queryBuilder();
        if (!where.isEmpty()) {
            switch (where) {
                case "name":
                    usersQueryBuilder.where(UsersDao.Properties.Name.like("%" + where + "%"));
                    break;
                case "mobile":
                    usersQueryBuilder.where(UsersDao.Properties.Mobile.like("%" + where + "%"));
                    break;
                case "phone":
                    usersQueryBuilder.where(UsersDao.Properties.Phone.like("%" + where + "%"));
                    break;
            }
        }
        if (!orderBy.isEmpty()) {
            switch (orderBy) {
                case "%new-user%":
                    usersQueryBuilder.orderDesc(UsersDao.Properties.Create);
                    break;
                default:
                    usersQueryBuilder.orderAsc(UsersDao.Properties.Name);
            }
        } else {
            usersQueryBuilder.orderAsc(UsersDao.Properties.Name);
        }

        return usersQueryBuilder.build().list();
    }

    private UsersDao setupDb() {
        DaoMaster.DevOpenHelper masterHelper = new DaoMaster.DevOpenHelper(this.context, config.DB_NAME, null);
        SQLiteDatabase db = masterHelper.getWritableDatabase();
        DaoMaster master = new DaoMaster(db);
        DaoSession masterSession=master.newSession();

        return masterSession.getUsersDao();
    }
}
