package com.midterm22nh12.shopapp.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.lifecycle.LiveData;

import com.midterm22nh12.shopapp.model.entity.restaurant;

import java.util.List;

@Dao
public interface restaurantDAO {
    @Insert
    void insertRestaurant(restaurant restaurant);

    @Update
    void updateRestaurant(restaurant restaurant);

    @Delete
    void deleteRestaurant(restaurant restaurant);

    @Query("SELECT * FROM restaurant")
    LiveData<List<restaurant>> getAllRestaurants();

    @Query("SELECT * FROM restaurant")
    List<restaurant> getAllRestaurants2();

    @Query("SELECT * FROM restaurant WHERE id = :id")
    LiveData<restaurant> getRestaurantById(String id);
    @Query("SELECT * FROM restaurant WHERE id = :id")
    restaurant getRestaurantById2(String id);

}
