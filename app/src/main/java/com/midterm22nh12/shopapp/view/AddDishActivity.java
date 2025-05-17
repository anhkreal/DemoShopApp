package com.midterm22nh12.shopapp.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.model.entity.dish;
import com.midterm22nh12.shopapp.model.database.AppDatabase;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class AddDishActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView dishImageView;
    private EditText nameEditText, descriptionEditText, priceEditText;
    private Button addDishButton;
    private Bitmap selectedImageBitmap;
    private String restaurantId;
    private ImageButton selectImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_dish);

        // Initialize UI components
        dishImageView = findViewById(R.id.dishImageView);
        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        addDishButton = findViewById(R.id.addDishButton);
        selectImageButton = findViewById(R.id.selectImageButton);

        // Get restaurantId from intent
        restaurantId = getIntent().getStringExtra("restaurantId");
        Log.d("AddDishActivity","restaurantId = " + restaurantId);
        // Set up button listeners
        selectImageButton.setOnClickListener(v -> openImagePicker());
        addDishButton.setOnClickListener(v -> saveDish());

        // Set up navigation icon listeners
        findViewById(R.id.itemHome).setOnClickListener(v -> navigateToHome());
        findViewById(R.id.itemMngt).setOnClickListener(v -> navigateToOrders());
        findViewById(R.id.itemInfo).setOnClickListener(v -> navigateToInfo());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                selectedImageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                dishImageView.setImageBitmap(selectedImageBitmap);
            } catch (Exception e) {
                Toast.makeText(this, "Không thể tải ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveDish() {
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String priceText = priceEditText.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || selectedImageBitmap == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin và chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] imageData = convertBitmapToByteArray(selectedImageBitmap);

        dish newDish = new dish(UUID.randomUUID().toString(), name, description, price, imageData, restaurantId);

        new Thread(() -> {
            AppDatabase.getInstance(this).dishDAO().insertDish(newDish);
            runOnUiThread(() -> {
                Toast.makeText(this, "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    // Navigation methods
    private void navigateToHome() {
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
