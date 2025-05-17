package com.midterm22nh12.shopapp.viewmodel;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.midterm22nh12.shopapp.model.dao.user_restaurantDAO;
import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.model.entity.dish;
import com.midterm22nh12.shopapp.model.entity.restaurant;
import com.midterm22nh12.shopapp.model.dao.restaurantDAO;
import com.midterm22nh12.shopapp.model.dao.dishDAO;
import com.midterm22nh12.shopapp.view.AddDishActivity;

import java.util.List;
import java.util.concurrent.Executors;

public class RestaurantOwnerHomeViewModel extends AndroidViewModel {

    private AppDatabase db;
    private user_restaurantDAO userRestaurantDAO;
    private restaurantDAO restaurantDAO;
    private dishDAO dishDAO;

    private MutableLiveData<String> restaurantIdLiveData = new MutableLiveData<>();
    private LiveData<restaurant> restaurantLiveData;
    private LiveData<List<dish>> dishesLiveData;

    public RestaurantOwnerHomeViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        userRestaurantDAO = db.user_restaurantDAO();
        restaurantDAO = db.restaurantDAO();
        dishDAO = db.dishDAO();
    }

    public void loadRestaurantId(String userId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            String rid = userRestaurantDAO.getRestaurantIdByUserId2(userId);
            restaurantIdLiveData.postValue(rid);
        });
    }

    public LiveData<String> getRestaurantIdLiveData() {
        return restaurantIdLiveData;
    }

    public LiveData<restaurant> getRestaurant(String restaurantId) {
        if (restaurantLiveData == null || !restaurantId.equals(restaurantIdLiveData.getValue())) {
            restaurantLiveData = restaurantDAO.getRestaurantById(restaurantId);
        }
        return restaurantLiveData;
    }

    public LiveData<List<dish>> getDishes(String restaurantId) {
        dishesLiveData = dishDAO.getDishesByRestaurantId(restaurantId);
        return dishesLiveData;
    }

    public void deleteDish(dish dish) {
        Executors.newSingleThreadExecutor().execute(() -> {
            dishDAO.deleteDishById(dish.getId());
        });
    }

    public void navigateToAddDish(Context context, String restaurantId) {
        Intent intent = new Intent(context, AddDishActivity.class);
        intent.putExtra("restaurantId", restaurantId);
        context.startActivity(intent);
    }
}
