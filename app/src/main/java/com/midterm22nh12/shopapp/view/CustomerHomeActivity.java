package com.midterm22nh12.shopapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.adapter.Quan1Adapter;
import com.midterm22nh12.shopapp.adapter.MonanAdapter;
import com.midterm22nh12.shopapp.model.dao.restaurantDAO;
import com.midterm22nh12.shopapp.model.dao.dishDAO;
import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.model.entity.restaurant;
import com.midterm22nh12.shopapp.model.entity.dish;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class CustomerHomeActivity extends AppCompatActivity {

    private TextView locationMessage;
    private RecyclerView restaurantList, dishList;
    private String userDistrict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        locationMessage = findViewById(R.id.location_message);
        restaurantList = findViewById(R.id.restaurant_list);
        dishList = findViewById(R.id.dish_list);

        restaurantList.setLayoutManager(new LinearLayoutManager(this));
        restaurantList.setAdapter(new Quan1Adapter(new ArrayList<>(), null));

        dishList.setLayoutManager(new LinearLayoutManager(this));
        dishList.setAdapter(new MonanAdapter(new ArrayList<>()));

        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userID = sharedPref.getString("userID", null);

        String searchQuery = getIntent().getStringExtra("search_query");

        if (userID != null) {
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                fetchUserDataWithSearch(userID, searchQuery.trim());
            } else {
                fetchUserData(userID);
            }
        }

        setupNavigation();
    }

    private void fetchUserData(String userID) {
        AppDatabase db = AppDatabase.getInstance(this);

        new Thread(() -> {
            userDistrict = db.userDAO().getUserByIdNow(userID).getDistrictName();

            runOnUiThread(() -> {
                if (userDistrict == null || userDistrict.isEmpty()) {
                    locationMessage.setVisibility(View.VISIBLE);
                } else {
                    locationMessage.setVisibility(View.GONE);
                }
            });

            setupRestaurantList(db.restaurantDAO());
            setupDishList(db.dishDAO());
        }).start();
    }

    private void fetchUserDataWithSearch(String userID, String searchQuery) {
        AppDatabase db = AppDatabase.getInstance(this);

        new Thread(() -> {
            userDistrict = db.userDAO().getUserByIdNow(userID).getDistrictName();

            runOnUiThread(() -> {
                if (userDistrict == null || userDistrict.isEmpty()) {
                    locationMessage.setVisibility(View.VISIBLE);
                } else {
                    locationMessage.setVisibility(View.GONE);
                }
            });

            // Tìm kiếm nhà hàng
            List<restaurant> allRestaurants = db.restaurantDAO().getAllRestaurants2();
            List<restaurant> matchedRestaurants = new ArrayList<>();
            List<restaurant> matchedRestaurantsSameDistrict = new ArrayList<>();
            for (restaurant res : allRestaurants) {
                if (res.getName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                        (res.getDescription() != null && res.getDescription().toLowerCase().contains(searchQuery.toLowerCase()))) {
                    if (userDistrict != null && userDistrict.equalsIgnoreCase(res.getDistrictName())) {
                        matchedRestaurantsSameDistrict.add(res);
                    } else {
                        matchedRestaurants.add(res);
                    }
                }
            }
            List<restaurant> resultRestaurants = new ArrayList<>();
            resultRestaurants.addAll(matchedRestaurantsSameDistrict);
            resultRestaurants.addAll(matchedRestaurants);
            if (resultRestaurants.size() > 10) {
                resultRestaurants = resultRestaurants.subList(0, 10);
            }

            final List<restaurant> finalResultRestaurants = resultRestaurants;


            // Tìm kiếm món ăn
            List<dish> allDishes = db.dishDAO().getAllDishes2();
            List<dish> matchedDishes = new ArrayList<>();
            List<dish> matchedDishesSameDistrict = new ArrayList<>();
            for (dish d : allDishes) {
                if (d.getName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                        (d.getDescription() != null && d.getDescription().toLowerCase().contains(searchQuery.toLowerCase()))) {
                    // Ưu tiên món ăn cùng quận user
                    restaurant res = db.restaurantDAO().getRestaurantById2(d.getRestaurantId());
                    if (res != null && userDistrict != null && userDistrict.equalsIgnoreCase(res.getDistrictName())) {
                        matchedDishesSameDistrict.add(d);
                    } else {
                        matchedDishes.add(d);
                    }
                }
            }
            List<dish> resultDishes = new ArrayList<>();
            resultDishes.addAll(matchedDishesSameDistrict);
            resultDishes.addAll(matchedDishes);
            if (resultDishes.size() > 10) {
                resultDishes = resultDishes.subList(0, 10);
            }

            final List<dish> finalResultDishes = resultDishes;

            runOnUiThread(() -> {
                Quan1Adapter adapter = new Quan1Adapter(finalResultRestaurants, userDistrict);
                restaurantList.setLayoutManager(new LinearLayoutManager(this));
                restaurantList.setAdapter(adapter);

                MonanAdapter dishAdapter = new MonanAdapter(finalResultDishes);
                dishList.setLayoutManager(new LinearLayoutManager(this));
                dishList.setAdapter(dishAdapter);
            });
        }).start();
    }

    private void setupRestaurantList(restaurantDAO restaurantDAO) {
        new Thread(() -> {
            List<restaurant> restaurants = restaurantDAO.getAllRestaurants2();
            if (restaurants != null && restaurants.size() > 10) {
                Collections.shuffle(restaurants);
                restaurants = restaurants.subList(0, 10);
            }
            final List<restaurant> finalRestaurants = restaurants;

            runOnUiThread(() -> {
                Quan1Adapter adapter = new Quan1Adapter(finalRestaurants, userDistrict);
                restaurantList.setLayoutManager(new LinearLayoutManager(this));
                restaurantList.setAdapter(adapter);
            });
        }).start();
    }


    private void setupDishList(dishDAO dishDAO) {
        new Thread(() -> {
            List<dish> dishes = dishDAO.getAllDishes2();
            if (dishes != null && dishes.size() > 10) {
                Collections.shuffle(dishes);
                dishes = dishes.subList(0, 10);
            }
            final List<dish> finalDishes= dishes;
            runOnUiThread(() -> {
                MonanAdapter adapter = new MonanAdapter(finalDishes);
                dishList.setLayoutManager(new LinearLayoutManager(this));
                dishList.setAdapter(adapter);
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
