package com.midterm22nh12.shopapp.model.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

@Entity(tableName = "contacts")
public class contact implements Serializable {
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    @ColumnInfo(name = "id")
    private String id;

    @SerializedName("facebook_link")
    @ColumnInfo(name = "facebook_link")
    private String FacebookLink;

    @SerializedName("zalo_link")
    @ColumnInfo(name = "zalo_link")
    private String ZaloLink;

    @SerializedName("user_id")
    @ColumnInfo(name = "user_id")
    private String userId;

    // Constructor
    public contact(String id, String FacebookLink, String ZaloLink, String userId) {
        this.id = id;
        this.FacebookLink = FacebookLink;
        this.ZaloLink = ZaloLink;
        this.userId = userId;
    }

    // Default constructor
    public contact() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFacebookLink() {
        return FacebookLink;
    }

    public void setFacebookLink(String FacebookLink) {
        this.FacebookLink = FacebookLink;
    }

    public String getZaloLink() {
        return ZaloLink;
    }

    public void setZaloLink(String ZaloLink) {
        this.ZaloLink = ZaloLink;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
