package com.midterm22nh12.shopapp.model.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "invoices")
public class invoice implements Serializable {
    @PrimaryKey
    @SerializedName("id")
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @SerializedName("restaurant_id")
    @ColumnInfo(name = "restaurant_id")
    private String restaurantId;

    @SerializedName("order_time")
    @ColumnInfo(name = "order_time")
    private Date orderTime; // Use Date for date/time representation

    @SerializedName("status")
    @ColumnInfo(name = "status")
    private String status; // Example: "Chờ đặt", "Chưa xác nhận", etc.

    @SerializedName("total_payment")
    @ColumnInfo(name = "total_payment")
    private int totalPayment;

    // Constructor
    public invoice(String id, String restaurantId, Date orderTime, String status, int totalPayment) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.orderTime = orderTime;
        this.status = status;
        this.totalPayment = totalPayment;
    }

    // Default constructor
    public invoice() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(int totalPayment) {
        this.totalPayment = totalPayment;
    }
}
