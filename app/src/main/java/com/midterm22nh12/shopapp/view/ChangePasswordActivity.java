package com.midterm22nh12.shopapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.model.dao.userDAO;
import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.model.entity.user;

public class ChangePasswordActivity extends AppCompatActivity {

    private String userId;
    private userDAO userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        userDao = AppDatabase.getInstance(this).userDAO();

        // Get userId from session
        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPref.getString("userID", null);

        EditText currentPassword = findViewById(R.id.currentPassword);
        EditText newPassword = findViewById(R.id.newPassword);
        EditText confirmNewPassword = findViewById(R.id.confirmNewPassword);
        Button saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            String currentPwd = currentPassword.getText().toString();
            String newPwd = newPassword.getText().toString();
            String confirmPwd = confirmNewPassword.getText().toString();

            if (newPwd.isEmpty() || confirmPwd.isEmpty() || currentPwd.isEmpty()) {
                Toast.makeText(this, "Hãy điền các trường còn trống", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPwd.equals(confirmPwd)) {
                Toast.makeText(this, "Mật khẩu mới và xác nhận không trùng nhau", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                user currentUser = userDao.getUserByIdNow(userId);
                if (currentUser != null && currentUser.getPassword().equals(currentPwd)) {
                    currentUser.setPassword(newPwd);
                    userDao.updateUser(currentUser);
                    runOnUiThread(() -> Toast.makeText(this, "Mật khẩu cập nhật thành công", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Mật khẩu cũ không khớp", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });

        // Navigation buttons
        findViewById(R.id.itemHome).setOnClickListener(v -> {
            Intent intent = new Intent(this, RestaurantOwnerHomeActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.itemMngt).setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderManagementActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.itemInfo).setOnClickListener(v -> {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        });
    }
}
