package com.midterm22nh12.shopapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.adapter.MonanAdapter;
import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.model.entity.dish;
import com.midterm22nh12.shopapp.model.entity.restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailActivity extends AppCompatActivity {

    private ImageView restaurantImageView;
    private TextView restaurantNameTextView, restaurantDescriptionTextView, deliveryInfoTextView;
    private RecyclerView dishRecyclerView;
    private String restaurantID;
    private restaurant currentRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaurant_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        restaurantImageView = findViewById(R.id.restaurantImageView);
        restaurantNameTextView = findViewById(R.id.restaurantNameTextView);
        restaurantDescriptionTextView = findViewById(R.id.restaurantDescriptionTextView);
        deliveryInfoTextView = findViewById(R.id.delivery_info);
        dishRecyclerView = findViewById(R.id.dishRecyclerView);

        dishRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dishRecyclerView.setAdapter(new MonanAdapter(new ArrayList<>()));

        // Nhận restaurantID từ intent
        restaurantID = getIntent().getStringExtra("restaurantID");
        if (restaurantID == null) {
            Toast.makeText(this, "Không tìm thấy nhà hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Truy vấn và hiển thị thông tin nhà hàng và danh sách món ăn
        loadRestaurantInfoAndDishes();

        // Sự kiện thanh điều hướng
        setupNavigation();
    }

    private void loadRestaurantInfoAndDishes() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            currentRestaurant = db.restaurantDAO().getRestaurantById2(restaurantID);
            List<dish> dishList = db.dishDAO().getDishesByRestaurantId2(restaurantID);
            Log.d("RestaurantDetailActivity", "Dishlist size:" + dishList.size());
            runOnUiThread(() -> {
                if (currentRestaurant != null) {
                    restaurantNameTextView.setText(currentRestaurant.getName());
                    restaurantDescriptionTextView.setText(currentRestaurant.getDescription());
                    // Hiển thị hình ảnh nếu có
                    byte[] imgBytes = currentRestaurant.getImageData();
                    if (imgBytes != null && imgBytes.length > 0) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                        restaurantImageView.setImageBitmap(bitmap);
                    } else {
                        restaurantImageView.setImageResource(R.drawable.default_restaurant_image);
                    }
                    // Phí vận chuyển và thời gian giao hàng = 0
                    deliveryInfoTextView.setText("Thời gian giao hàng: 0 phút, phí 0đ");
                }
                MonanAdapter adapter = new MonanAdapter(dishList);
                dishRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                dishRecyclerView.setAdapter(adapter);
                Log.d("RestaurantDetailActivity", "Dishlist size:" + dishList.size());
                // Hiển thị danh sách món ăn
//                MonanAdapter adapter = new MonanAdapter(dishList, dish -> {
//                    // Sự kiện nhấn nút chi tiết ở mỗi món ăn
//                    Intent intent = new Intent(RestaurantDetailActivity.this, DishDetailActivity.class);
//                    intent.putExtra("dishID", dish.getId());
//                    startActivity(intent);
//                });
//                dishRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//                dishRecyclerView.setAdapter(adapter);
            });
        }).start();
    }

    private void setupNavigation() {
        findViewById(R.id.itemHome).setOnClickListener(v -> {
            startActivity(new Intent(this, CustomerHomeActivity.class));
        });
        findViewById(R.id.itemSearch).setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
        });
        findViewById(R.id.itemCart).setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });
        findViewById(R.id.itemInfo).setOnClickListener(v -> {
            startActivity(new Intent(this, CustomerInfoActivity.class));
        });
    }
}
