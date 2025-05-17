package com.midterm22nh12.shopapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.model.entity.user;

public class LocationActivity extends AppCompatActivity {

    private EditText streetNameEditText;
    private Spinner districtSpinner;
    private Button saveButton;
    private AppDatabase database;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        streetNameEditText = findViewById(R.id.streetNameEditText);
        districtSpinner = findViewById(R.id.districtSpinner);
        saveButton = findViewById(R.id.saveButton);

        // Set up Spinner
        String[] districts = {"Liên Chiểu", "Thanh Khê", "Hải Châu", "Ngũ Hành Sơn", "Hòa Vang", "Cẩm Lệ", "Sơn Trà"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(adapter);

        // Initialize database
        database = AppDatabase.getInstance(this);

        // Get session
        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPref.getString("userID", null);

        if (userId != null) {
            new Thread(() -> {
                user currentUser = database.userDAO().getUserByIdNow(userId);
                if (currentUser != null) {
                    runOnUiThread(() -> {
                        streetNameEditText.setText(currentUser.getStreetName());
                        int position = adapter.getPosition(currentUser.getDistrictName());
                        if (position >= 0) districtSpinner.setSelection(position);
                    });
                }
            }).start();
        }

        saveButton.setOnClickListener(v -> updateAddress());

        findViewById(R.id.itemHome).setOnClickListener(v -> navigateToHome());
        findViewById(R.id.itemMngt).setOnClickListener(v -> navigateToOrders());
        findViewById(R.id.itemInfo).setOnClickListener(v -> navigateToInfo());
    }

    private void updateAddress() {
        String streetName = streetNameEditText.getText().toString().trim();
        String districtName = districtSpinner.getSelectedItem().toString();

        if (streetName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên đường", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            database.userDAO().updateUserAddress(userId, streetName, districtName);
            runOnUiThread(() -> Toast.makeText(this, "Cập nhật địa chỉ thành công", Toast.LENGTH_SHORT).show());
        }).start();
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, RestaurantOwnerHomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToOrders() {
        Intent intent = new Intent(this, OrderManagementActivity.class);
        startActivity(intent);
    }

    private void navigateToInfo() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }
}
