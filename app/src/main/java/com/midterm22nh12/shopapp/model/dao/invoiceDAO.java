package com.midterm22nh12.shopapp.model.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.midterm22nh12.shopapp.model.entity.invoice;
import java.util.List;
import androidx.lifecycle.LiveData;

@Dao
public interface invoiceDAO {
    @Insert
    void insertInvoice(invoice invoice);

    @Update
    void updateInvoice(invoice invoice);

    @Delete
    void deleteInvoice(invoice invoice);

    @Query("SELECT * FROM invoices WHERE id = :id")
    LiveData<invoice> getInvoiceById(String id);

    @Query("SELECT * FROM invoices")
    LiveData<List<invoice>> getAllInvoices();

    @Query("SELECT * FROM invoices")
    List<invoice> getAllInvoices2();
}

