package com.midterm22nh12.shopapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.model.dao.userDAO;
import com.midterm22nh12.shopapp.model.entity.user;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUpViewModel extends AndroidViewModel {

    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> navigateToLogin = new MutableLiveData<>();
    private final userDAO userDAO;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        userDAO = db.userDAO();
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public LiveData<Boolean> getNavigateToLogin() {
        return navigateToLogin;
    }

    public void signUp(String name, String email, String password, String phone, String role) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            message.setValue("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        // Kiểm tra định dạng email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            message.setValue("Email không đúng định dạng");
            return;
        }

        // Kiểm tra số điện thoại đủ 10 số, bắt đầu bằng 0
        if (!phone.matches("^0\\d{9}$")) {
            message.setValue("Số điện thoại phải đủ 10 số và bắt đầu bằng 0");
            return;
        }

        executor.execute(() -> {
            user existingUser = userDAO.getUserByEmailNow(email);
            if (existingUser != null) {
                message.postValue("Thông tin email đã tồn tại, xin mời nhập lại");
                return;
            }
            user existingPhoneUser = userDAO.getUserByPhoneNow(phone);
            if (existingPhoneUser != null) {
                message.postValue("Số điện thoại đã được sử dụng, xin mời nhập lại");
                return;
            }
            user newUser = new user();
            newUser.setId(UUID.randomUUID().toString());
            newUser.setName(name);
            newUser.setGmail(email);
            newUser.setPassword(password);
            newUser.setRole(role.equals("Khách hàng") ? 0 : 1);
            newUser.setPhoneNumber(phone);

            userDAO.insertUser(newUser);
            message.postValue("Đăng kí thành công! Vui lòng đăng nhập.");
            navigateToLogin.postValue(true);
        });
    }
}
