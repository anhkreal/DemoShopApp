package com.midterm22nh12.shopapp.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.lifecycle.LiveData;

import com.midterm22nh12.shopapp.model.entity.restaurant;
import com.midterm22nh12.shopapp.model.entity.user_restaurant;

import java.util.List;

@Dao
public interface user_restaurantDAO {
    @Insert
    void insert(user_restaurant userRestaurant);

    @Update
    void update(user_restaurant userRestaurant);

    @Delete
    void delete(user_restaurant userRestaurant);

    @Query("SELECT * FROM user_restaurant WHERE id = :id")
    LiveData<user_restaurant> getById(String id);
    @Query("SELECT * FROM user_restaurant WHERE id = :id")
    user_restaurant getById2(String id);

    @Query("SELECT * FROM user_restaurant")
    LiveData<List<user_restaurant>> getAll();

    @Query("SELECT restaurant_id FROM user_restaurant WHERE user_id = :userId LIMIT 1")
    LiveData<String> getRestaurantIdByUserId(String userId);
    @Query("SELECT restaurant_id FROM user_restaurant WHERE user_id = :userId LIMIT 1")
    String getRestaurantIdByUserId2(String userId);
}

