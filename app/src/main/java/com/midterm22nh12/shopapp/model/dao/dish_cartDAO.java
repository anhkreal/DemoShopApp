package com.midterm22nh12.shopapp.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.midterm22nh12.shopapp.model.entity.dish_cart;
import java.util.List;
import androidx.lifecycle.LiveData;

@Dao
public interface dish_cartDAO {
    @Insert
    void insert(dish_cart dishCart);

    @Update
    void update(dish_cart dishCart);

    @Delete
    void delete(dish_cart dishCart);

    @Query("SELECT * FROM dish_cart")
    LiveData<List<dish_cart>> getAllDishCarts();

    @Query("SELECT * FROM dish_cart WHERE id = :id")
    LiveData<dish_cart> getDishCartById(String id);

    @Query("SELECT * FROM dish_cart WHERE id = :id")
    dish_cart getDishCartById2(String id);

    @Query("DELETE FROM dish_cart WHERE id = :id")
    void deleteById(String id);

    @Query("UPDATE dish_cart SET quantity = :quantity WHERE id = :id")
    void updateQuantity(String id, int quantity);
}
