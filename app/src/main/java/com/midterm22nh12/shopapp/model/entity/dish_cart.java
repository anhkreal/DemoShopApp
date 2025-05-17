package com.midterm22nh12.shopapp.model.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

@Entity(tableName = "dish_cart")
public class dish_cart implements Serializable {
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    @ColumnInfo(name = "id")
    private String id;

    @SerializedName("dish_id")
    @ColumnInfo(name = "dish_id")
    private String dishId;

    @SerializedName("quantity")
    @ColumnInfo(name = "quantity")
    private int quantity;

    @SerializedName("note")
    @ColumnInfo(name = "note")
    private String note;

    @SerializedName("is_selected")
    @ColumnInfo(name = "is_selected")
    private boolean isSelected;

    // Default constructor
    public dish_cart() {
    }

    // Parameterized constructor
    public dish_cart(String id, String dishId, int quantity, String note) {
        this.id = id;
        this.dishId = dishId;
        this.quantity = quantity;
        this.note = note;
        this.isSelected = false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isSelected() { return isSelected; }

    public void setSelected(boolean selected) { isSelected = selected; }
}
