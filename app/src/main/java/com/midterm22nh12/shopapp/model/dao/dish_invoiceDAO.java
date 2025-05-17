package com.midterm22nh12.shopapp.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.midterm22nh12.shopapp.model.entity.dish_invoice;
import java.util.List;
import androidx.lifecycle.LiveData;

@Dao
public interface dish_invoiceDAO {
    @Insert
    long insertDishInvoice(dish_invoice dishInvoice);

    @Update
    int updateDishInvoice(dish_invoice dishInvoice);

    @Delete
    int deleteDishInvoice(dish_invoice dishInvoice);

    @Query("SELECT * FROM dish_invoice WHERE id = :id")
    LiveData<dish_invoice> getDishInvoiceById(String id);

    @Query("SELECT * FROM dish_invoice")
    LiveData<List<dish_invoice>> getAllDishInvoices();

    @Query("SELECT * FROM dish_invoice")
    List<dish_invoice> getAllDishInvoices2();

    @Query("DELETE FROM dish_invoice WHERE dish_cart_id = :dishCartId")
    void deleteByDishCartId(String dishCartId);
}
