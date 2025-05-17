package com.midterm22nh12.shopapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.adapter.QuanDonHangAdapter;
import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.model.entity.*;

import java.util.*;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrderHistory;
    private String userID;
    private QuanDonHangAdapter adapterOrderHistory;

    // Navigation
    private ImageView itemHome, itemSearch, itemCart, itemInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvOrderHistory = findViewById(R.id.rv_order_history);

        // Setup navigation
        itemHome = findViewById(R.id.itemHome);
        itemSearch = findViewById(R.id.itemSearch);
        itemCart = findViewById(R.id.itemCart);
        itemInfo = findViewById(R.id.itemInfo);

        itemHome.setOnClickListener(v -> startActivity(new Intent(this, CustomerHomeActivity.class)));
        itemSearch.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        itemCart.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        itemInfo.setOnClickListener(v -> startActivity(new Intent(this, CustomerInfoActivity.class)));

        loadUserSession();
        if (userID == null) {
            Toast.makeText(this, "Không tìm thấy userID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadOrderHistory();
    }

    private void loadUserSession() {
        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        userID = sharedPref.getString("userID", null);
    }

    private void loadOrderHistory() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<dish_invoice> allDishInvoices = db.dish_invoiceDAO().getAllDishInvoices2();
            List<invoice> allInvoices = db.invoiceDAO().getAllInvoices2();

            List<dish_invoice> userDishInvoices = new ArrayList<>();
            for (dish_invoice di : allDishInvoices) {
                if (userID.equals(di.getCustomerId()) && di.getInvoiceId() != null) {
                    userDishInvoices.add(di);
                }
            }

            Map<String, List<dish_invoice>> invoiceMap = new HashMap<>();
            for (dish_invoice di : userDishInvoices) {
                invoiceMap.computeIfAbsent(di.getInvoiceId(), k -> new ArrayList<>()).add(di);
            }

            List<QuanDonHangAdapter.QuanDonHangItem> donHangItems = new ArrayList<>();
            for (invoice inv : allInvoices) {
                if (!invoiceMap.containsKey(inv.getId())) continue;
                if (!"Hoàn tất".equals(inv.getStatus())) continue;

                List<dish_invoice> dishInvoices = invoiceMap.get(inv.getId());
                List<dish_cart> dishCarts = new ArrayList<>();
                List<dish> dishes = new ArrayList<>();
                restaurant resOrigin = db.restaurantDAO().getRestaurantById2(inv.getRestaurantId());

                for (dish_invoice di : dishInvoices) {
                    dish_cart cart = db.dish_cartDAO().getDishCartById2(di.getDishCartId());
                    if (cart != null) {
                        dishCarts.add(cart);
                        dishes.add(db.dishDAO().getDishById2(cart.getDishId()));
                    }
                }

                // Tạo restaurant mới chỉ để hiển thị tên, trạng thái và ngày đặt hàng
                restaurant res = new restaurant();
                if (resOrigin != null) {
                    res.setName(
                        resOrigin.getName() + ", " +
                        inv.getStatus() + "\n" +
                        (inv.getOrderTime() != null ?
                            new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(inv.getOrderTime())
                            : "")
                    );
                } else {
                    res.setName(
                        "Nhà hàng không xác định, " +
                        inv.getStatus() + "\n" +
                        (inv.getOrderTime() != null ?
                            new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(inv.getOrderTime())
                            : "")
                    );
                }

                donHangItems.add(new QuanDonHangAdapter.QuanDonHangItem(res, inv, dishCarts, dishes));
            }

            runOnUiThread(() -> {
                adapterOrderHistory = new QuanDonHangAdapter(donHangItems);
                rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
                rvOrderHistory.setAdapter(adapterOrderHistory);
            });
        }).start();
    }
}
