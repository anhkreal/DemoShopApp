package com.midterm22nh12.shopapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.model.entity.contact;

import java.util.UUID;

public class SocialAccountsActivity extends AppCompatActivity {

    private EditText facebookEditText, zaloEditText;
    private Button saveButton;
    private AppDatabase database;
    private String userId;
    private boolean isExist = false;
    private contact currentContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_accounts);

        facebookEditText = findViewById(R.id.facebookEditText);
        zaloEditText = findViewById(R.id.zaloEditText);
        saveButton = findViewById(R.id.saveButton);

        database = AppDatabase.getInstance(this);

        // Get userID from session
        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPref.getString("userID", null);

        if (userId != null) {
            new Thread(() -> {
                currentContact = database.contactDAO().getContactByUserId(userId);
                if (currentContact != null) {
                    isExist = true;
                    runOnUiThread(() -> {
                        facebookEditText.setText(currentContact.getFacebookLink());
                        zaloEditText.setText(currentContact.getZaloLink());
                    });
                }
            }).start();
        }

        saveButton.setOnClickListener(v -> handleSave());

        // Navigation bar buttons
        findViewById(R.id.itemHome).setOnClickListener(v -> navigateToHome());
        findViewById(R.id.itemMngt).setOnClickListener(v -> navigateToOrders());
        findViewById(R.id.itemInfo).setOnClickListener(v -> navigateToInfo());
    }

    private void handleSave() {
        String facebookLink = facebookEditText.getText().toString().trim();
        String zaloLink = zaloEditText.getText().toString().trim();

        if (facebookLink.isEmpty() || zaloLink.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            if (isExist) {
                currentContact.setFacebookLink(facebookLink);
                currentContact.setZaloLink(zaloLink);
                database.contactDAO().updateContact(currentContact);
                runOnUiThread(() -> Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show());
            } else {
                contact newContact = new contact(UUID.randomUUID().toString(), facebookLink, zaloLink, userId);
                database.contactDAO().insertContact(newContact);
                runOnUiThread(() -> Toast.makeText(this, "Thêm mới thành công", Toast.LENGTH_SHORT).show());
            }
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
