package com.midterm22nh12.shopapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.databinding.ActivityCustomerInfoBinding;
import com.midterm22nh12.shopapp.databinding.ActivityInfoBinding;

public class CustomerInfoActivity extends AppCompatActivity {

    private ActivityCustomerInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set text for each item using ViewBinding
        binding.itemPersonalInfo.text.setText("Thông tin cá nhân");
        binding.itemChangePassword.text.setText("Đổi mật khẩu");
        binding.itemLocation.text.setText("Địa chỉ");
        binding.itemLogout.text.setText("Đăng xuất");

        // Set click listeners for items
        binding.itemPersonalInfo.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerPersonalInfoActivity.class);
            startActivity(intent);
        });
        binding.itemChangePassword.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerChangePasswordActivity.class);
            startActivity(intent);
        });
        binding.itemLocation.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerLocationActivity.class);
            startActivity(intent);
        });
        binding.itemLogout.getRoot().setOnClickListener(v -> {
            SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
            sharedPref.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Set click listeners for bottom navigation
        binding.navigationLayout.itemHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerHomeActivity.class);
            startActivity(intent);
            finish();
        });
        binding.navigationLayout.itemSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });binding.navigationLayout.itemCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });
        binding.navigationLayout.itemInfo.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerInfoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
