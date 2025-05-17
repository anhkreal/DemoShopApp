package com.midterm22nh12.shopapp.model.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import androidx.room.ColumnInfo;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity
public class user implements Serializable {
    @PrimaryKey
    @SerializedName("id")
    @NotNull
    @ColumnInfo(name = "id")
    private String id;

    @SerializedName("gmail")
    @ColumnInfo(name = "gmail")
    private String gmail;

    @SerializedName("password")
    @ColumnInfo(name = "password")
    private String password;

    @SerializedName("role")
    @ColumnInfo(name = "role")
    private int role; // 0 - khách, 1 - chủ

    @SerializedName("name")
    @ColumnInfo(name = "name")
    private String name;

    @SerializedName("street_name")
    @ColumnInfo(name = "street_name")
    private String streetName;

    @SerializedName("district_name")
    @ColumnInfo(name = "district_name")
    private String districtName;

    @SerializedName("phone_number")
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    // Hàm dựng
    public user() {
    }

    public user(String id, String gmail, String password, int role, String name, String streetName, String districtName, String phoneNumber) {
        this.id = id;
        this.gmail = gmail;
        this.password = password;
        this.role = role;
        this.name = name;
        this.streetName = streetName;
        this.districtName = districtName;
        this.phoneNumber = phoneNumber;
    }

    // Getters và Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
