package com.midterm22nh12.shopapp.model.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import androidx.room.ColumnInfo;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity(tableName = "user_restaurant")
public class user_restaurant implements Serializable {
    @PrimaryKey
    @SerializedName("id")
    @NotNull
    @ColumnInfo(name = "id")
    private String id;

    @SerializedName("user_id")
    @ColumnInfo(name = "user_id")
    private String userId;

    @SerializedName("restaurant_id")
    @ColumnInfo(name = "restaurant_id")
    private String restaurantId;

    // Default constructor
    public user_restaurant() {
    }

    // Parameterized constructor
    public user_restaurant(String id, String userID, String restaurantID) {
        this.id = id;
        this.userId = userID;
        this.restaurantId = restaurantID;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
