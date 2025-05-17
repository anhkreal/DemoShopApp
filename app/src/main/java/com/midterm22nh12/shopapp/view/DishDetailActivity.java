package com.midterm22nh12.shopapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.model.entity.dish;
import com.midterm22nh12.shopapp.model.entity.user;
import com.midterm22nh12.shopapp.model.entity.dish_cart;
import com.midterm22nh12.shopapp.model.entity.dish_invoice;

import java.util.UUID;

public class DishDetailActivity extends AppCompatActivity {

    private ImageView imgDish;
    private TextView tvDishName, tvDishDescription, tvDishPrice;
    private EditText edtQuantity, edtNote;
    private Button btnRestaurantDetail, btnAddToCart;
    private String userID, dishID;
    private dish currentDish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dish_detail);
        // tránh giao diện chồng lên thanh systemBars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgDish = findViewById(R.id.imgDish);
        tvDishName = findViewById(R.id.tvDishName);
        tvDishDescription = findViewById(R.id.tvDishDescription);
        tvDishPrice = findViewById(R.id.tvDishPrice);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtNote = findViewById(R.id.edtNote);
        btnRestaurantDetail = findViewById(R.id.btnRestaurantDetail);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        // Lấy userID từ session
        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        userID = sharedPref.getString("userID", null);

        // Lấy dishID từ intent
        dishID = getIntent().getStringExtra("dishID");

        if (userID == null || dishID == null) {
            Toast.makeText(this, "Thiếu thông tin người dùng hoặc món ăn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Truy vấn user và dish, hiển thị thông tin món ăn
        loadDishInfo();

        // Sự kiện nút Chi tiết nhà hàng
        btnRestaurantDetail.setOnClickListener(v -> {
            if (currentDish != null) {
                Intent intent = new Intent(DishDetailActivity.this, RestaurantDetailActivity.class);
                intent.putExtra("restaurantID", currentDish.getRestaurantId());
                startActivity(intent);
            }
        });

        // Sự kiện nút Thêm vào giỏ hàng
        btnAddToCart.setOnClickListener(v -> {
            String quantityStr = edtQuantity.getText().toString().trim();
            int quantity = 1;
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) throw new NumberFormatException();
            } catch (Exception e) {
                Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            String note = edtNote.getText().toString().trim();
            final int  quantityFinal = quantity;
            new Thread(() -> {
                try {
                    AppDatabase db = AppDatabase.getInstance(this);
                    // Tạo mới dish_cart
                    String dishCartId = UUID.randomUUID().toString();
                    dish_cart dishCart = new dish_cart(dishCartId, dishID, quantityFinal, note);
                    db.dish_cartDAO().insert(dishCart);

                    // Tạo mới dish_invoice
                    String dishInvoiceId = UUID.randomUUID().toString();
                    dish_invoice dishInvoice = new dish_invoice(dishInvoiceId, userID, dishCartId, null);
                    db.dish_invoiceDAO().insertDishInvoice(dishInvoice);

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        // Chuyển sang RestaurantDetailActivity
                        Intent intent = new Intent(DishDetailActivity.this, RestaurantDetailActivity.class);
                        intent.putExtra("restaurantID", currentDish.getRestaurantId());
                        startActivity(intent);
                        finish();
                    });
                } catch (Exception ex) {
                    runOnUiThread(() -> Toast.makeText(this, "Thêm vào giỏ hàng thất bại", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });

        // Sự kiện thanh điều hướng
        setupNavigation();
    }

    private void loadDishInfo() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            currentDish = db.dishDAO().getDishById2(dishID);
            if (currentDish != null) {
                runOnUiThread(() -> {
                    tvDishName.setText(currentDish.getName());
                    tvDishDescription.setText(currentDish.getDescription());
                    tvDishPrice.setText(String.format("%,d VNĐ", currentDish.getPrice()));
                    // Hiển thị hình ảnh từ byte[]
                    byte[] imgBytes = currentDish.getImage();
                    if (imgBytes != null && imgBytes.length > 0) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                        imgDish.setImageBitmap(bitmap);
                    } else {
                        imgDish.setImageResource(R.drawable.ic_launcher_background);
                    }
                });
            }
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
