package com.midterm22nh12.shopapp.model.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import androidx.room.ColumnInfo;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity
public class restaurant implements Serializable {
    @PrimaryKey
    @SerializedName("id")
    @NotNull
    @ColumnInfo(name = "id")
    private String id;

    @SerializedName("name")
    @ColumnInfo(name = "name")
    private String name;

    @SerializedName("description")
    @ColumnInfo(name = "description")
    private String description;

    @SerializedName("street_name")
    @ColumnInfo(name = "street_name")
    private String streetName;

    @SerializedName("district_name")
    @ColumnInfo(name = "district_name")
    private String districtName;

    @SerializedName("image_data")
    @ColumnInfo(name = "image_data")
    private byte[] imageData;

    // Hàm dựng
    public restaurant() {
    }

    public restaurant(String id, String name, String description, String streetName, String districtName, byte[] imageData) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.streetName = streetName;
        this.districtName = districtName;
        this.imageData = imageData;
    }

    // Getters và Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
}
