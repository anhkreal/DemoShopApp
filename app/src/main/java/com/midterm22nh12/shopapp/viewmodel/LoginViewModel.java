package com.midterm22nh12.shopapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.midterm22nh12.shopapp.model.dao.userDAO;
import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.model.entity.user;

public class LoginViewModel extends AndroidViewModel {
    private final userDAO userDao;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDAO();
    }

    public LiveData<user> validateUser(String email, String password) {
        return userDao.validateUser(email, password);
    }
}
