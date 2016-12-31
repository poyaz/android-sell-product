package com.example.woods.amin.Controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.woods.amin.Database.DaoMaster;
import com.example.woods.amin.Database.DaoSession;
import com.example.woods.amin.Database.Images;
import com.example.woods.amin.Database.ImagesDao;
import com.example.woods.amin.Database.Products;
import com.example.woods.amin.Database.ProductsDao;
import com.example.woods.amin.config;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Date;
import java.util.List;


public class ProductsController {
    private ProductsDao productsDao;
    private Context context;
    private Long id = -1L;

    public ProductsController(Context context) {
        this.context = context;
        this.productsDao = setupDb();
    }

    public void addNewProduct(Bundle data) {
        Date date = new Date();

        Products products = new Products(
                null,
                data.getString("title"),
                data.getString("info"),
                data.getString("price"),
                data.getInt("count"),
                data.getString("off"),
                date,
                date,
                false
        );
        this.id = this.productsDao.insert(products);
    }

    public void editProduct(Long id, Bundle data) {
        if (id == null || id == -1L)
            return;

        Products productInfo = this.getProductInfoById(id);

        productInfo.setTitle(data.getString("title"));
        productInfo.setInfo(data.getString("info"));
        productInfo.setPrice(data.getString("price"));
        productInfo.setCount(data.getInt("count"));
        productInfo.setOff(data.getString("off"));
        productInfo.setUpdate(new Date());
        productInfo.setTop(false);

        productInfo.update();
        productInfo.refresh();
    }

    public Long getId() {
        return this.id;
    }

    public List<Products> getProductList(String where) {
        QueryBuilder<Products> productsQueryBuilder = this.productsDao.queryBuilder();
        productsQueryBuilder.join(Images.class, ImagesDao.Properties.Product_id)
                .where(ImagesDao.Properties.Primary.eq(true));
        if (!where.isEmpty()) {
            switch (where) {
                case "top":
                    productsQueryBuilder.where(ProductsDao.Properties.Top.eq(true));
                    break;
                case "exist":
                    productsQueryBuilder.where(ProductsDao.Properties.Count.gt(0));
                    break;
            }
        }

        return productsQueryBuilder.orderDesc(ProductsDao.Properties.Update).build().list();
    }

    public Products getProductInfoById(Long id) {
        QueryBuilder<Products> productsQueryBuilder = this.productsDao.queryBuilder();
        productsQueryBuilder.where(ProductsDao.Properties.Id.eq(id))
                .join(Images.class, ImagesDao.Properties.Product_id)
                .where(ImagesDao.Properties.Primary.eq(true));

        List<Products> products = productsQueryBuilder.build().list();

        if (products != null && products.size() == 1) {
            return products.get(0);
        } else {
            return null;
        }
    }

    private ProductsDao setupDb() {
        DaoMaster.DevOpenHelper masterHelper = new DaoMaster.DevOpenHelper(this.context, config.DB_NAME, null);
        SQLiteDatabase db = masterHelper.getWritableDatabase();
        DaoMaster master = new DaoMaster(db);
        DaoSession masterSession = master.newSession();

        return masterSession.getProductsDao();
    }
}
