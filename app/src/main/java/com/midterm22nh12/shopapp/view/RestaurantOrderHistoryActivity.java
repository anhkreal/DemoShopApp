package com.midterm22nh12.shopapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.adapter.QuanGioHang2Adapter;
import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.model.entity.dish;
import com.midterm22nh12.shopapp.model.entity.dish_cart;
import com.midterm22nh12.shopapp.model.entity.dish_invoice;
import com.midterm22nh12.shopapp.model.entity.invoice;
import com.midterm22nh12.shopapp.model.entity.restaurant;
import com.midterm22nh12.shopapp.model.dao.user_restaurantDAO;
import com.midterm22nh12.shopapp.model.dao.invoiceDAO;
import com.midterm22nh12.shopapp.model.dao.dish_invoiceDAO;
import com.midterm22nh12.shopapp.model.dao.dish_cartDAO;
import com.midterm22nh12.shopapp.model.dao.dishDAO;
import com.midterm22nh12.shopapp.model.dao.restaurantDAO;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class RestaurantOrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvHoanTat;
    private ImageView itemHome, itemMngt, itemInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_order_history);

        rvHoanTat = findViewById(R.id.rv_donhang_hoantat);

        // Thanh điều hướng
        itemHome = findViewById(R.id.itemHome);
        itemMngt = findViewById(R.id.itemMngt);
        itemInfo = findViewById(R.id.itemInfo);

        itemHome.setOnClickListener(v -> startActivity(new Intent(this, RestaurantOwnerHomeActivity.class)));
        itemMngt.setOnClickListener(v -> startActivity(new Intent(this, OrderManagementActivity.class)));
        itemInfo.setOnClickListener(v -> startActivity(new Intent(this, InfoActivity.class)));

        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
            String userID = sharedPref.getString("userID", null);
            user_restaurantDAO urDao = db.user_restaurantDAO();
            String restaurantId = urDao.getRestaurantIdByUserId2(userID);
            if (restaurantId == null) {
                runOnUiThread(() -> Toast.makeText(this, "Không tìm thấy nhà hàng!", Toast.LENGTH_SHORT).show());
                return;
            }
            invoiceDAO invDao = db.invoiceDAO();
            dish_invoiceDAO diDao = db.dish_invoiceDAO();
            dish_cartDAO dcDao = db.dish_cartDAO();
            dishDAO dDao = db.dishDAO();
            restaurantDAO rDao = db.restaurantDAO();

            List<invoice> allInvoices = invDao.getAllInvoices2();
            List<invoice> invHoanTat = new ArrayList<>();
            for (invoice inv : allInvoices) {
                if (restaurantId.equals(inv.getRestaurantId()) && "Hoàn tất".equals(inv.getStatus())) {
                    invHoanTat.add(inv);
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            List<QuanGioHang2Adapter.QuanGioHangItem> hoanTatItems = new ArrayList<>();
            for (invoice inv : invHoanTat) {
                List<dish_invoice> dishInvoices = diDao.getAllDishInvoices2();
                List<dish_cart> carts = new ArrayList<>();
                List<dish> dishes = new ArrayList<>();
                String customerPhone = "";
                for (dish_invoice di : dishInvoices) {
                    if (inv.getId().equals(di.getInvoiceId())) {
                        dish_cart cart = dcDao.getDishCartById2(di.getDishCartId());
                        if (cart != null) {
                            carts.add(cart);
                            dish d = dDao.getDishById2(cart.getDishId());
                            if (d != null) dishes.add(d);
                        }
                        // Lấy số điện thoại người mua từ customerID
                        if (customerPhone.isEmpty() && di.getCustomerId() != null) {
                            com.midterm22nh12.shopapp.model.entity.user customer = db.userDAO().getUserByIdNow(di.getCustomerId());
                            if (customer != null && customer.getPhoneNumber() != null)
                                customerPhone = customer.getPhoneNumber();
                        }
                    }
                }
                // Tạo restaurant giả để truyền số điện thoại và thời gian đặt hàng vào tên
                restaurant fakeRes = new restaurant();
                String timeStr = "";
                if (inv.getOrderTime() != null) {
                    timeStr = sdf.format(inv.getOrderTime());
                }
                fakeRes.setName("SĐT: " + customerPhone + "\nThời gian: " + timeStr);
                hoanTatItems.add(new QuanGioHang2Adapter.QuanGioHangItem(fakeRes, carts, dishes));
            }

            runOnUiThread(() -> {
                rvHoanTat.setLayoutManager(new LinearLayoutManager(this));
                rvHoanTat.setAdapter(new QuanGioHang2Adapter(hoanTatItems, null));
            });
        }).start();
    }
}
