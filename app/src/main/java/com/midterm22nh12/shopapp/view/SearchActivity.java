package com.midterm22nh12.shopapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.midterm22nh12.shopapp.R;

public class SearchActivity extends AppCompatActivity {

    private EditText edtSearch;
    private ImageView btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(v -> {
            String keyword = edtSearch.getText().toString().trim();
            if (keyword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, CustomerHomeActivity.class);
            intent.putExtra("search_query", keyword);
            startActivity(intent);
        });

        // Thanh điều hướng dưới cùng
        findViewById(R.id.itemHome).setOnClickListener(v -> {
            startActivity(new Intent(this, CustomerHomeActivity.class));
        });
        findViewById(R.id.itemSearch).setOnClickListener(v -> {
            // Đang ở trang này, không làm gì
        });
        findViewById(R.id.itemCart).setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });
        findViewById(R.id.itemInfo).setOnClickListener(v -> {
            startActivity(new Intent(this, CustomerInfoActivity.class));
        });
    }
}
