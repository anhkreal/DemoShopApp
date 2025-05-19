package com.midterm22nh12.shopapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.model.entity.restaurant;
import com.midterm22nh12.shopapp.model.entity.user_restaurant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import android.view.View;
import android.widget.ImageButton;

public class RestaurantInfoActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private boolean isExist = false;
    private EditText nameEditText, descriptionEditText, streetNameEditText;
    private Spinner districtSpinner;
    private ImageView restaurantImageView;
    private Button actionButton;
    private AppDatabase database;
    private String userId, restaurantId;
    private Bitmap selectedBitmap = null;
    private ImageButton imagePickerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);

        // Initialize UI components
        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        streetNameEditText = findViewById(R.id.streetNameEditText);
        districtSpinner = findViewById(R.id.districtSpinner);
        restaurantImageView = findViewById(R.id.restaurantImageView);
        actionButton = findViewById(R.id.actionButton);

        // Set up Spinner
        String[] districts = {"Liên Chiểu", "Thanh Khê", "Hải Châu", "Ngũ Hành Sơn", "Hòa Vang", "Cẩm Lệ", "Sơn Trà"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, districts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(adapter);

        // Set up navigation buttons
        findViewById(R.id.itemHome).setOnClickListener(v -> reloadActivity());
        findViewById(R.id.itemMngt).setOnClickListener(v -> navigateToOrders());
        findViewById(R.id.itemInfo).setOnClickListener(v -> navigateToInfo());

        // Initialize database
        database = AppDatabase.getInstance(this);

        // Get session
        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPref.getString("userID", null);
        Log.d("RestaurantInfoActivity", "userID = " + userId);
        if (userId != null) {
            new Thread(() -> {
                restaurantId = database.user_restaurantDAO().getRestaurantIdByUserId2(userId);
                Log.d("RestaurantInfoActivity", "restaurantID = " + restaurantId);
                if (restaurantId != null) {
                    restaurant restaurant = database.restaurantDAO().getRestaurantById2(restaurantId);
                    if (restaurant != null) {
                        isExist = true;
                        runOnUiThread(() -> {
                            populateRestaurantInfo(restaurant);
                            actionButton.setText("Lưu");
                        });
                        Log.d("RestaurantInfoActivity", "isExist1 = " + (isExist == true ? "true" : "false"));
                    }
                }
                Log.d("RestaurantInfoActivity", "isExist = " + (isExist == true ? "true" : "false"));
            }).start();
        }

        // Upload image handler
//        restaurantImageView.setOnClickListener(v -> openImagePicker());

        // Save or add button
        actionButton.setText(isExist ? "Lưu" : "Thêm mới");
        actionButton.setOnClickListener(v -> {
            if (isExist) {
                updateRestaurant();
            } else {
                addNewRestaurant();
            }
        });

        imagePickerButton = findViewById(R.id.imagePickerButton);
        imagePickerButton.setOnClickListener(v -> openImagePicker());

        // Set default image if no image is selected
        if (selectedBitmap == null) {
            restaurantImageView.setImageResource(R.drawable.default_restaurant_image);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                selectedBitmap = BitmapFactory.decodeStream(inputStream);
                restaurantImageView.setImageBitmap(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Không thể chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void populateRestaurantInfo(restaurant restaurant) {
        nameEditText.setText(restaurant.getName());
        descriptionEditText.setText(restaurant.getDescription());
        streetNameEditText.setText(restaurant.getStreetName());

        if (restaurant.getDistrictName() != null) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) districtSpinner.getAdapter();
            int position = adapter.getPosition(restaurant.getDistrictName());
            if (position >= 0) districtSpinner.setSelection(position);
        }

        if (restaurant.getImageData() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(restaurant.getImageData(), 0, restaurant.getImageData().length);
            restaurantImageView.setImageBitmap(bitmap);
            selectedBitmap = bitmap;
        } else {
            restaurantImageView.setImageResource(R.drawable.default_restaurant_image);
        }
    }

    private void updateRestaurant() {
        new Thread(() -> {
            byte[] imageData = selectedBitmap != null ? bitmapToByteArray(selectedBitmap) : null;

            restaurant updatedRestaurant = new restaurant(
                    restaurantId,
                    nameEditText.getText().toString(),
                    descriptionEditText.getText().toString(),
                    streetNameEditText.getText().toString(),
                    districtSpinner.getSelectedItem().toString(),
                    imageData
            );

            database.restaurantDAO().updateRestaurant(updatedRestaurant);

            runOnUiThread(() -> {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                reloadActivity(); // reload lại trang sau khi cập nhật
            });
        }).start();
    }

    private void addNewRestaurant() {
        new Thread(() -> {
            restaurantId = UUID.randomUUID().toString();
            byte[] imageData = selectedBitmap != null ? bitmapToByteArray(selectedBitmap) : null;

            restaurant newRestaurant = new restaurant(
                    restaurantId,
                    nameEditText.getText().toString(),
                    descriptionEditText.getText().toString(),
                    streetNameEditText.getText().toString(),
                    districtSpinner.getSelectedItem().toString(),
                    imageData
            );
            database.restaurantDAO().insertRestaurant(newRestaurant);

            user_restaurant newUserRestaurant = new user_restaurant(
                    UUID.randomUUID().toString(),
                    userId,
                    restaurantId
            );
            database.user_restaurantDAO().insert(newUserRestaurant);

            runOnUiThread(() -> {
                Toast.makeText(this, "Thêm mới thành công", Toast.LENGTH_SHORT).show();
                reloadActivity(); // reload lại trang sau khi thêm mới
            });
        }).start();
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void reloadActivity() {
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

