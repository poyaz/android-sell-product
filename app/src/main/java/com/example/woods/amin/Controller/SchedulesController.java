package com.example.woods.amin.Controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.woods.amin.Database.DaoMaster;
import com.example.woods.amin.Database.DaoSession;
import com.example.woods.amin.Database.Schedules;
import com.example.woods.amin.Database.SchedulesDao;
import com.example.woods.amin.config;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Calendar;
import java.util.List;

public class SchedulesController {
    private SchedulesDao schedulesDao;
    private Context context;
    private Long id;

    public SchedulesController(Context context) {
        this.context = context;
        this.schedulesDao = setupDb();
    }

    public void addNewSchedule(Bundle data) {
        Schedules schedules = new Schedules(
                null,
                data.getString("name"),
                data.getString("description"),
                data.getString("date"),
                data.getBoolean("enable"),
                data.getLong("uid")
        );
        this.id = this.schedulesDao.insert(schedules);
    }

    public Long getId() {
        return this.id;
    }

    public List<Schedules> getSchedulesList() {
        return _getSchedulesList(-1, "");
    }

    public List<Schedules> getSchedulesList(String search) {
        return _getSchedulesList(-1, search);
    }

    public List<Schedules> getSchedulesList(Integer enable) {
        return _getSchedulesList(enable, "");
    }

    public List<Schedules> getSchedulesList(Integer enable, String search) {
        return _getSchedulesList(enable, search);
    }

    private List<Schedules> _getSchedulesList(Integer enable, String search) {
        QueryBuilder<Schedules> schedulesQueryBuilder = this.schedulesDao.queryBuilder();

        if (!search.isEmpty()) {
            schedulesQueryBuilder.where(SchedulesDao.Properties.Title.like("%" + search + "%"));
        }
        if (enable != -1) {
            schedulesQueryBuilder.where(SchedulesDao.Properties.Enable.eq(enable == 1));
        }

        return schedulesQueryBuilder.build().list();
    }

    public void updateSchedules(List<Schedules> schedules) {
        if (schedules.size() == 0)
            return;

        for (Schedules schedule : schedules) {
            schedule.setEnable(false);
            schedule.update();
            schedule.refresh();
        }
    }

    public void updateSchedulesById(Long sid, Bundle data) {
        if (sid == null || sid == -1L)
            return;

        Schedules scheduleInfo = this.getScheduleInfoById(sid);
        scheduleInfo.setDescription(data.getString("description", ""));
        scheduleInfo.setUnix_time(data.getString("date", ""));
        scheduleInfo.setEnable(data.getBoolean("enable", false));
        scheduleInfo.setUser_id(data.getLong("uid", -1L));

        scheduleInfo.update();
        scheduleInfo.refresh();
    }

    public Schedules getScheduleInfoById(long id) {
        List<Schedules> schedules = this.schedulesDao.queryBuilder().where(SchedulesDao.Properties.Id.eq(id)).build().list();

        if (schedules != null && schedules.size() == 1) {
            return schedules.get(0);
        } else {
            return null;
        }
    }

    public void deleteSchedule(List<Long> delete) {
        if (delete.size() == 0)
            return;

        this.schedulesDao.deleteByKeyInTx(delete);
    }

    public List<Schedules> getSchedulesListOfAlarm() {
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.SECOND, -120);
        endCalendar.add(Calendar.SECOND, 59);

        return this.schedulesDao.queryBuilder()
                .where(SchedulesDao.Properties.Unix_time.between(startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis()),
                        SchedulesDao.Properties.Enable.eq(true))
                .build().list();
    }

    private SchedulesDao setupDb() {
        DaoMaster.DevOpenHelper masterHelper = new DaoMaster.DevOpenHelper(this.context, config.DB_NAME, null);
        SQLiteDatabase db = masterHelper.getWritableDatabase();
        DaoMaster master = new DaoMaster(db);
        DaoSession masterSession = master.newSession();

        return masterSession.getSchedulesDao();
    }
}
