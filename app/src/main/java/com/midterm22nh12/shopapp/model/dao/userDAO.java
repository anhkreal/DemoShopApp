package com.midterm22nh12.shopapp.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.midterm22nh12.shopapp.model.entity.user;

import java.util.List;

@Dao
public interface userDAO {
    @Query("SELECT * FROM user")
    LiveData<List<user>> getAllUsers();

    @Query("SELECT * FROM user WHERE id = :id")
    LiveData<user> getUserById(String id);

    @Query("SELECT * FROM user WHERE gmail = :email AND password = :password LIMIT 1")
    LiveData<user> validateUser(String email, String password);

    @Query("SELECT * FROM user WHERE gmail = :email")
    LiveData<user> getUserByEmail(String email);

    @Query("SELECT * FROM user WHERE gmail = :email")
    user getUserByEmailNow(String email);

    @Query("UPDATE user SET name = :name WHERE id = :id")
    void updateUserName(String id, String name);

    @Query("SELECT * FROM user WHERE id = :id")
    user getUserByIdNow(String id);

    @Query("SELECT * FROM user WHERE phone_number = :phone LIMIT 1")
    user getUserByPhoneNow(String phone);

    @Insert
    void insertUser(user user);

    @Update
    void updateUser(user user);

    @Delete
    void deleteUser(user user);
    
    @Query("UPDATE user SET street_name = :streetName, district_name = :districtName WHERE id = :id")
    void updateUserAddress(String id, String streetName, String districtName);

    @Query("UPDATE user SET phone_number = :phone WHERE id = :id")
    void updateUserPhone(String id, String phone);

}
