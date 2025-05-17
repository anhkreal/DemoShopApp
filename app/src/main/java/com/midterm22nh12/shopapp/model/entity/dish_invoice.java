package com.midterm22nh12.shopapp.model.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import androidx.room.ColumnInfo;

@Entity(tableName = "dish_invoice")
public class dish_invoice implements Serializable {
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    @ColumnInfo(name = "id")
    private String id;
    @SerializedName("customer_id")
    @ColumnInfo(name = "customer_id")
    private String customerId;

    @SerializedName("dish_cart_id")
    @ColumnInfo(name = "dish_cart_id")
    private String dishCartId;

    @SerializedName("invoice_id")
    @ColumnInfo(name = "invoice_id")
    private String invoiceId;

    // Constructor
    public dish_invoice(String id, String Customer_id, String dishCartId, String invoiceId) {
        this.id = id;
        this.customerId = Customer_id;
        this.dishCartId = dishCartId;
        this.invoiceId = invoiceId;
    }

    // Default constructor
    public dish_invoice() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDishCartId() {
        return dishCartId;
    }

    public void setDishCartId(String dishCartId) {
        this.dishCartId = dishCartId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }
}
