package com.midterm22nh12.shopapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.databinding.ActivityInfoBinding;

public class InfoActivity extends AppCompatActivity {

    private ActivityInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set text for each item using ViewBinding
        binding.itemPersonalInfo.text.setText("Thông tin cá nhân");
        binding.itemChangePassword.text.setText("Đổi mật khẩu");
        binding.itemLocation.text.setText("Địa chỉ");
        binding.itemSocialAccounts.text.setText("Tài khoản mạng xã hội");
        binding.itemRestaurantInfo.text.setText("Thông tin nhà hàng");
        binding.itemLogout.text.setText("Đăng xuất");

        // Set click listeners for items
        binding.itemPersonalInfo.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(this, PersonalInfoActivity.class);
            startActivity(intent);
        });
        binding.itemChangePassword.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
        });
        binding.itemLocation.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(this, LocationActivity.class);
            startActivity(intent);
        });
        binding.itemSocialAccounts.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(this, SocialAccountsActivity.class);
            startActivity(intent);
        });
        binding.itemRestaurantInfo.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(this, RestaurantInfoActivity.class);
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
            Intent intent = new Intent(this, RestaurantOwnerHomeActivity.class);
            startActivity(intent);
            finish();
        });
        binding.navigationLayout.itemMngt.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderManagementActivity.class);
            startActivity(intent);
        });
        binding.navigationLayout.itemInfo.setOnClickListener(v -> {
            Toast.makeText(this, "Bạn đang ở trang Thông tin", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
