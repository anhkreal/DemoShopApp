package com.midterm22nh12.shopapp.view;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.model.dao.userDAO;
import com.midterm22nh12.shopapp.model.entity.user;
import com.midterm22nh12.shopapp.view.OrderManagementActivity;
import com.midterm22nh12.shopapp.view.RestaurantOwnerHomeActivity;

public class PersonalInfoActivity extends AppCompatActivity {

    private EditText nameEditText, gmailEditText, phoneEditText;
    private Button saveButton;
    private String userId;
    private userDAO userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_info);

        // Initialize views
        nameEditText = findViewById(R.id.editTextName);
        gmailEditText = findViewById(R.id.editTextGmail);
        phoneEditText = findViewById(R.id.editTextPhone);
        saveButton = findViewById(R.id.buttonSave);

        AppDatabase db = AppDatabase.getInstance(this);
        userDao = db.userDAO();

        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPref.getString("userID", null);

        // Load user info
        new Thread(() -> {
            user currentUser = userDao.getUserByIdNow(userId);
            if (currentUser != null) {
                runOnUiThread(() -> {
                    nameEditText.setText(currentUser.getName());
                    gmailEditText.setText(currentUser.getGmail());
                    gmailEditText.setEnabled(false);
                    phoneEditText.setText(currentUser.getPhoneNumber());
                });
            }
        }).start();

        saveButton.setOnClickListener(v -> {
            String updatedName = nameEditText.getText().toString().trim();
            String updatedPhone = phoneEditText.getText().toString().trim();

            if (updatedName.isEmpty()) {
                Toast.makeText(this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!updatedPhone.matches("^0\\d{9}$")) {
                Toast.makeText(this, "Số điện thoại phải đủ 10 số và bắt đầu bằng 0", Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(() -> {
                user phoneUser = userDao.getUserByPhoneNow(updatedPhone);
                user currentUser = userDao.getUserByIdNow(userId);
                if (phoneUser != null && (currentUser == null || !updatedPhone.equals(currentUser.getPhoneNumber()))) {
                    runOnUiThread(() -> Toast.makeText(this, "Số điện thoại đã được sử dụng", Toast.LENGTH_SHORT).show());
                    return;
                }
                userDao.updateUserName(userId, updatedName);
                userDao.updateUserPhone(userId, updatedPhone);
                runOnUiThread(() -> Toast.makeText(this, "Thông tin đã được cập nhật", Toast.LENGTH_SHORT).show());
            }).start();
        });

        // Thanh điều hướng
        findViewById(R.id.itemHome).setOnClickListener(v -> {
            startActivity(new Intent(this, RestaurantOwnerHomeActivity.class));
        });
        findViewById(R.id.itemMngt).setOnClickListener(v -> {
            startActivity(new Intent(this, OrderManagementActivity.class));
        });
        findViewById(R.id.itemInfo).setOnClickListener(v -> {
            startActivity(new Intent(this, InfoActivity.class));
        });
    }
}
