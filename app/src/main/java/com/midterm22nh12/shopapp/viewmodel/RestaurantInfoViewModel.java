package com.midterm22nh12.shopapp.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.model.entity.restaurant;
import com.midterm22nh12.shopapp.model.entity.user_restaurant;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class RestaurantInfoViewModel extends AndroidViewModel {

    private final AppDatabase database;

    private final MutableLiveData<restaurant> restaurantLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isExistLiveData = new MutableLiveData<>(false);

    public RestaurantInfoViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
    }

    public LiveData<restaurant> getRestaurantLiveData() {
        return restaurantLiveData;
    }

    public LiveData<Boolean> getIsExistLiveData() {
        return isExistLiveData;
    }

    public void loadRestaurant(String userId) {
        new Thread(() -> {
            user_restaurant userRestaurant = database.user_restaurantDAO().getById2(userId);
            if (userRestaurant != null) {
                String restaurantId = userRestaurant.getRestaurantId();
                restaurant res = database.restaurantDAO().getRestaurantById2(restaurantId);
                if (res != null) {
                    isExistLiveData.postValue(true);
                    restaurantLiveData.postValue(res);
                }
            }
        }).start();
    }

    public void saveRestaurant(String userId, String name, String desc, String street, String district, Bitmap bitmap, boolean isExist) {
        new Thread(() -> {
            byte[] imageData = bitmap != null ? bitmapToByteArray(bitmap) : null;
            if (isExist) {
                restaurant current = restaurantLiveData.getValue();
                if (current != null) {
                    current.setName(name);
                    current.setDescription(desc);
                    current.setStreetName(street);
                    current.setDistrictName(district);
                    current.setImageData(imageData);
                    database.restaurantDAO().updateRestaurant(current);
                }
            } else {
                String restaurantId = UUID.randomUUID().toString();
                restaurant newRes = new restaurant(restaurantId, name, desc, street, district, imageData);
                database.restaurantDAO().insertRestaurant(newRes);
                database.user_restaurantDAO().insert(new user_restaurant(UUID.randomUUID().toString(), userId, restaurantId));
                isExistLiveData.postValue(true);
                restaurantLiveData.postValue(newRes);
            }
        }).start();
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
