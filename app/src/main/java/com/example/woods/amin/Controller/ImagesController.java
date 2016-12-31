package com.example.woods.amin.Controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;

import com.example.woods.amin.Database.DaoMaster;
import com.example.woods.amin.Database.DaoSession;
import com.example.woods.amin.Database.Images;
import com.example.woods.amin.Database.ImagesDao;
import com.example.woods.amin.config;

import java.util.List;

public class ImagesController {
    private ImagesDao imagesDao;
    private Context context;

    public ImagesController(Context context) {
        this.context = context;
        this.imagesDao = setupDb();
    }

    public void addNewImages(Long id, String imagePrimary, Bundle data) {
        if (id == null || id == -1L)
            return;

        String[] imagesList = data.getStringArray("images");
        if (imagesList != null) {
            Images[] images = new Images[imagesList.length + 1];
            images[0] = new Images(null, imagePrimary, true, true, id);

            int i = 1;
            for (String getImage : imagesList) {
                images[i++] = new Images(null, getImage, false, true, id);
            }

            this.imagesDao.insertInTx(images);
        }
    }

    public void editProductImage(Long id, String imagePrimary, Bundle data) {
        if (id == null || id == -1L)
            return;

        this.imagesDao.queryBuilder().where(ImagesDao.Properties.Product_id.eq(id))
                .buildDelete().executeDeleteWithoutDetachingEntities();

        this.addNewImages(id, imagePrimary, data);
    }

    public List<Images> getProductImage(Long id) {
        if (id == null || id == -1L)
            return null;

        return this.imagesDao.queryBuilder().where(ImagesDao.Properties.Product_id.eq(id), ImagesDao.Properties.Primary.eq(false))
                .build().list();
    }

    private ImagesDao setupDb() {
        DaoMaster.DevOpenHelper masterHelper = new DaoMaster.DevOpenHelper(this.context, config.DB_NAME, null);
        SQLiteDatabase db = masterHelper.getWritableDatabase();
        DaoMaster master = new DaoMaster(db);
        DaoSession masterSession = master.newSession();

        return masterSession.getImagesDao();
    }
}
