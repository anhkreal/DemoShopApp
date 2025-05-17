package com.midterm22nh12.shopapp.model.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import android.content.Context;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;


import com.midterm22nh12.shopapp.model.converter.DateConverter;
import com.midterm22nh12.shopapp.model.dao.contactDAO;
import com.midterm22nh12.shopapp.model.dao.dishDAO;
import com.midterm22nh12.shopapp.model.dao.dish_cartDAO;
import com.midterm22nh12.shopapp.model.dao.dish_invoiceDAO;
import com.midterm22nh12.shopapp.model.dao.invoiceDAO;
import com.midterm22nh12.shopapp.model.dao.restaurantDAO;
import com.midterm22nh12.shopapp.model.dao.userDAO;
import com.midterm22nh12.shopapp.model.dao.user_restaurantDAO;
import com.midterm22nh12.shopapp.model.entity.contact;
import com.midterm22nh12.shopapp.model.entity.dish;
import com.midterm22nh12.shopapp.model.entity.dish_cart;
import com.midterm22nh12.shopapp.model.entity.dish_invoice;
import com.midterm22nh12.shopapp.model.entity.invoice;
import com.midterm22nh12.shopapp.model.entity.restaurant;
import com.midterm22nh12.shopapp.model.entity.user;
import com.midterm22nh12.shopapp.model.entity.user_restaurant;

@Database(entities = {
        contact.class,
        dish.class,
        dish_cart.class,
        dish_invoice.class,
        invoice.class,
        restaurant.class,
        user.class,
        user_restaurant.class
}, version = 2, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
        }
    };

    private static AppDatabase instance;

    public abstract contactDAO contactDAO();
    public abstract dishDAO dishDAO();
    public abstract dish_cartDAO dish_cartDAO();
    public abstract dish_invoiceDAO dish_invoiceDAO();
    public abstract invoiceDAO invoiceDAO();
    public abstract restaurantDAO restaurantDAO();
    public abstract user_restaurantDAO user_restaurantDAO();
    public abstract userDAO userDAO();


    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "shop_app5")
                    .fallbackToDestructiveMigration()
                    .build();

        }
        return instance;
    }

}