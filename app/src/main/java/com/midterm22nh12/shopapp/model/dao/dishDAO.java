package com.midterm22nh12.shopapp.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.midterm22nh12.shopapp.model.entity.dish;
import java.util.List;
import androidx.lifecycle.LiveData;

@Dao
public interface dishDAO {
    @Insert
    void insertDish(dish dish);

    @Update
    void updateDish(dish dish);

    @Query("UPDATE dish SET is_deleted = 1 WHERE id = :dishId")
    void deleteDishById(String dishId);

    @Query("SELECT * FROM dish")
    LiveData<List<dish>> getAllDishes();

    @Query("SELECT * FROM dish WHERE is_deleted = 0 OR is_deleted IS NULL")
    List<dish> getAllDishes2();

    @Query("SELECT * FROM dish WHERE id = :id")
    LiveData<dish> getDishById(String id);

    @Query("SELECT * FROM dish WHERE id = :id")
    dish getDishById2(String id);

    @Query("SELECT * FROM dish WHERE restaurant_id = :restaurantId AND (is_deleted = 0 OR is_deleted IS NULL)")
    LiveData<List<dish>> getDishesByRestaurantId(String restaurantId);

    @Query("SELECT * FROM dish WHERE restaurant_id = :restaurantId AND (is_deleted = 0 OR is_deleted IS NULL)")
    List<dish> getDishesByRestaurantId2(String restaurantId);
}
