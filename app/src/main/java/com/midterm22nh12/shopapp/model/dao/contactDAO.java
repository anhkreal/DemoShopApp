package com.midterm22nh12.shopapp.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.midterm22nh12.shopapp.model.entity.contact;
import java.util.List;
import androidx.lifecycle.LiveData;

@Dao
public interface contactDAO {
    @Insert
    void insertContact(contact newContact);

    @Query("SELECT * FROM contacts WHERE id = :id")
    LiveData<contact> getContactById(String id);

    @Query("SELECT * FROM contacts")
    LiveData<List<contact>> getAllContacts();

    @Query("SELECT * FROM contacts WHERE user_id = :userId LIMIT 1")
    contact getContactByUserId(String userId);

    @Update
    void updateContact(contact updatedContact);

    @Delete
    void deleteContact(contact contactToDelete);
}
