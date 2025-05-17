package com.midterm22nh12.shopapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.adapter.DishUserAdapter;
import com.midterm22nh12.shopapp.model.entity.dish;
import com.midterm22nh12.shopapp.model.entity.restaurant;
import com.midterm22nh12.shopapp.viewmodel.RestaurantOwnerHomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class RestaurantOwnerHomeActivity extends AppCompatActivity {

    private RestaurantOwnerHomeViewModel viewModel;

    private ImageView restaurantImageView;
    private TextView restaurantNameTextView, restaurantDescriptionTextView, noRestaurantTextView;
    private Button addDishButton;
    private RecyclerView dishRecyclerView;
    private View restaurantInfoLayout;

    private String userId;
    private String restaurantId;
    private DishUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_owner_home);

        restaurantImageView = findViewById(R.id.restaurantImageView);
        restaurantNameTextView = findViewById(R.id.restaurantNameTextView);
        restaurantDescriptionTextView = findViewById(R.id.restaurantDescriptionTextView);
//        noRestaurantTextView = findViewById(R.id.noRestaurantTextView);
        addDishButton = findViewById(R.id.addDishButton);
        dishRecyclerView = findViewById(R.id.dishRecyclerView);
        restaurantInfoLayout = findViewById(R.id.restaurantInfoLayout);

        viewModel = new ViewModelProvider(this).get(RestaurantOwnerHomeViewModel.class);

        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPref.getString("userID", null);

        if (userId == null) {
            Toast.makeText(this, "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize the adapter with an empty list
        adapter = new DishUserAdapter(new ArrayList<>(), this::onEditDish, this::onDeleteDish);
        dishRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dishRecyclerView.setAdapter(adapter);

        viewModel.getRestaurantIdLiveData().observe(this, rid -> {
            restaurantId = rid;

            // Luôn hiển thị layout
            restaurantInfoLayout.setVisibility(View.VISIBLE);
            dishRecyclerView.setVisibility(View.VISIBLE);
//            noRestaurantTextView.setVisibility(View.GONE); // Không dùng nữa

            if (rid == null) {
                // Không có nhà hàng
                restaurantNameTextView.setText("Không có nhà hàng");
                restaurantDescriptionTextView.setText("Bạn cần vào 'Thông tin' để thêm mới nhà hàng");
                restaurantImageView.setImageResource(R.drawable.default_restaurant_image);
                addDishButton.setEnabled(false);
                adapter.updateDishes(new ArrayList<>()); // RecyclerView trống
            } else {
                // Có nhà hàng
                addDishButton.setEnabled(true);

                viewModel.getRestaurant(rid).observe(this, restaurant -> {
                    if (restaurant != null) {
                        restaurantNameTextView.setText(restaurant.getName());
                        restaurantDescriptionTextView.setText(restaurant.getDescription());

                        if (restaurant.getImageData() != null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(
                                    restaurant.getImageData(), 0, restaurant.getImageData().length
                            );
                            restaurantImageView.setImageBitmap(bitmap);
                        } else {
                            restaurantImageView.setImageResource(R.drawable.default_restaurant_image);
                        }
                    }
                });

                viewModel.getDishes(rid).observe(this, dishes -> {
                    if (dishes != null && !dishes.isEmpty()) {
                        adapter.updateDishes(dishes);
                    } else {
                        adapter.updateDishes(new ArrayList<>());
                    }
                });
            }
        });




        viewModel.loadRestaurantId(userId);

        addDishButton.setOnClickListener(v -> {
            if (restaurantId != null) {
                Intent intent = new Intent(this, AddDishActivity.class);
                intent.putExtra("restaurantId", restaurantId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Không thể thêm món ăn. Nhà hàng không tồn tại.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.itemHome).setOnClickListener(v -> reloadActivity());
        findViewById(R.id.itemMngt).setOnClickListener(v -> navigateToOrders());
        findViewById(R.id.itemInfo).setOnClickListener(v -> navigateToInfo());
    }

    private void onEditDish(dish dish) {
        Intent intent = new Intent(this, EditDishActivity.class);
        intent.putExtra("dishId", dish.getId());
        startActivity(intent);
    }

    private void onDeleteDish(dish dish) {
        viewModel.deleteDish(dish);
        Toast.makeText(this, "Xoá món ăn thành công", Toast.LENGTH_SHORT).show();
    }

    private void reloadActivity() {
        startActivity(new Intent(this, RestaurantOwnerHomeActivity.class));
        finish();
    }

    private void navigateToOrders() {
        startActivity(new Intent(this, OrderManagementActivity.class));
    }

    private void navigateToInfo() {

        startActivity(new Intent(this, InfoActivity.class));
    }


}
