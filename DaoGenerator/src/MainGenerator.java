/**
 * Created by woods on 10/23/2016.
 */

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;

/**
 * Schema 1: create database
 * Schema 2: add images table
 * Schema 3: add primary column to images table
 * Schema 4: add count column to orderProducts table
 * Schema 5: add relation one between order and user table
 * Schema 6: add id in orderProducts table
 * Schema 7: change relation between schedules and users table
 * Schema 8: fix relation between schedules and users table
 */
public class MainGenerator {
    public static void main(String[] args) {
        Schema schema = new Schema(8, "com.example.woods.amin.Database");
        schema.enableKeepSectionsByDefault();

        addTables(schema);

        try {
            new DaoGenerator().generateAll(schema, "app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTables(final Schema schema) {
        Entity users = addUsers(schema);
        Entity products = addProducts(schema);
        Entity orders = addOrders(schema);
        Entity orderProducts = addOrderProducts(schema);
        Entity schedules = addSchedules(schema);
        Entity settings = addSettings(schema);
        Entity images = addImages(schema);

        Property userId1 = orders.addLongProperty("user_id").notNull().getProperty();
        users.addToMany(orders, userId1, "userOrders");
        orders.addToOne(users, userId1, "orderUsers");

        Property productId1 = orderProducts.addLongProperty("product_id").notNull().getProperty();
        orderProducts.addToOne(products, productId1, "orderProductProduct");

        Property orderId = orderProducts.addLongProperty("order_id").notNull().getProperty();
        orders.addToMany(orderProducts, orderId, "orderOrderProducts");

        Property userId2 = schedules.addLongProperty("user_id").notNull().getProperty();
        schedules.addToOne(users, userId2, "userSchedules");

        Property productId2 = images.addLongProperty("product_id").notNull().getProperty();
        products.addToMany(images, productId2, "productImages");
    }

    private static Entity addUsers(final Schema schema) {
        Entity users = schema.addEntity("Users");
        users.addIdProperty().primaryKey().autoincrement();
        users.addStringProperty("name").notNull();
        users.addStringProperty("address").notNull();
        users.addStringProperty("phone").notNull();
        users.addStringProperty("mobile").notNull();
        users.addDateProperty("create").notNull();

        return users;
    }

    private static Entity addProducts(final Schema schema) {
        Entity products = schema.addEntity("Products");
        products.addIdProperty().primaryKey().autoincrement();
        products.addStringProperty("title").notNull();
        products.addStringProperty("info").notNull();
        products.addStringProperty("price").notNull();
        products.addIntProperty("count").notNull();
        products.addStringProperty("off");
        products.addDateProperty("create").notNull();
        products.addDateProperty("update").notNull();
        products.addBooleanProperty("top").notNull();

        return products;
    }

    private static Entity addOrders(final Schema schema) {
        Entity orders = schema.addEntity("Orders");
        orders.addIdProperty().primaryKey().autoincrement();
        orders.addStringProperty("first_price");
        orders.addStringProperty("last_price");
        orders.addStringProperty("off");
        orders.addIntProperty("status").notNull();
        orders.addDateProperty("create");

        return orders;
    }

    private static Entity addOrderProducts(final Schema schema) {
        Entity orderProducts = schema.addEntity("OrderProducts");
        orderProducts.addIdProperty().primaryKey().autoincrement();
        orderProducts.addStringProperty("price").notNull();
        orderProducts.addIntProperty("count").notNull();
        orderProducts.addStringProperty("off");

        return orderProducts;
    }

    private static Entity addSchedules(final Schema schema) {
        Entity schedules = schema.addEntity("Schedules");
        schedules.addIdProperty().primaryKey().autoincrement();
        schedules.addStringProperty("title").notNull();
        schedules.addStringProperty("description");
        schedules.addStringProperty("unix_time").notNull();
        schedules.addBooleanProperty("enable").notNull();

        return schedules;
    }

    private static Entity addSettings(final Schema schema) {
        Entity settings = schema.addEntity("Settings");
        settings.addStringProperty("key");
        settings.addStringProperty("value");

        return settings;
    }

    private static Entity addImages(final Schema schema) {
        Entity images = schema.addEntity("Images");
        images.addIdProperty().primaryKey().autoincrement();
        images.addStringProperty("uri");
        images.addBooleanProperty("primary");
        images.addBooleanProperty("enable");

        return images;
    }
}
