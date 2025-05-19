package com.midterm22nh12.shopapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
import com.midterm22nh12.shopapp.adapter.QuanDonHangAdapter;
import com.midterm22nh12.shopapp.adapter.QuanGioHangAdapter;
import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.model.entity.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class CartActivity extends AppCompatActivity {

    // UI components
    private RecyclerView rvDonHangDaDat, rvDonHangChuaDat;
    private TextView tvTongCong;
    private Button btnMuaHang, btnLichSuDonHang;

    // User session
    private String userID;
    private String userDistrict; // Thêm biến lưu quận user

    // State
    private String selectedRestaurantId = null;
    private final Set<String> selectedCartIds = new HashSet<>();
    private int tongTien = 0;

    private int tongCong = 0;

    // Adapters
    private QuanDonHangAdapter adapterDonHangDaDat;
    private QuanGioHangAdapter adapterDonHangChuaDat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout_giohang), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadUserSession();

        if (userID == null) {
            Toast.makeText(this, "Không tìm thấy userID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadDonHangDaDat();
        loadDonHangChuaDat();
        setupListeners();
        setupNavigation();
    }

    private void initViews() {
        rvDonHangDaDat = findViewById(R.id.rv_donhang_dadat);
        rvDonHangChuaDat = findViewById(R.id.rv_donhang_chuadat);
        tvTongCong = findViewById(R.id.tv_tongcong);
        btnMuaHang = findViewById(R.id.btn_muahang);
        btnLichSuDonHang = findViewById(R.id.btn_lichsu_donhang);
    }

    private void loadUserSession() {
        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        userID = sharedPref.getString("userID", null);

        // Lấy userDistrict
        AppDatabase db = AppDatabase.getInstance(this);
        userDistrict = null;
        if (userID != null) {
            new Thread(() -> {
                user userObj = db.userDAO().getUserByIdNow(userID);
                if (userObj != null) userDistrict = userObj.getDistrictName();
            }).start();
        }
    }

    private void setupListeners() {
        btnLichSuDonHang.setOnClickListener(v -> {
            startActivity(new Intent(this, OrderHistoryActivity.class));
        });

        btnMuaHang.setOnClickListener(v -> {
            if (selectedCartIds.isEmpty() || selectedRestaurantId == null) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một món trong cùng một quán!", Toast.LENGTH_SHORT).show();
                return;
            }

            AppDatabase db = AppDatabase.getInstance(this);
            String invoiceId = UUID.randomUUID().toString();
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            new Thread(() -> {
                Date dateObj = null;
                try {
                    dateObj = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(currentTime);
                } catch (Exception ignored) {}

                invoice newInvoice = new invoice(invoiceId, selectedRestaurantId, dateObj, "Chờ xác nhận", tongCong);
                Log.d("Cart", "tongCong: " + tongCong);
                tongCong = 0;
                tongTien = 0;
                db.invoiceDAO().insertInvoice(newInvoice);

                List<dish_invoice> allDishInvoices = db.dish_invoiceDAO().getAllDishInvoices2();
                for (dish_invoice di : allDishInvoices) {
                    if (selectedCartIds.contains(di.getDishCartId()) && di.getInvoiceId() == null) {
                        di.setInvoiceId(invoiceId);
                        db.dish_invoiceDAO().updateDishInvoice(di);
                    }
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                    loadDonHangDaDat();
                    loadDonHangChuaDat();
                });
            }).start();
        });
    }

    private void loadDonHangDaDat() {
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
                if (!"Chờ xác nhận".equals(inv.getStatus()) && !"Chờ nhận hàng".equals(inv.getStatus())) continue;

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
                adapterDonHangDaDat = new QuanDonHangAdapter(donHangItems);
                rvDonHangDaDat.setLayoutManager(new LinearLayoutManager(this));
                rvDonHangDaDat.setAdapter(adapterDonHangDaDat);
            });
        }).start();
    }

    private void loadDonHangChuaDat() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<dish_invoice> allDishInvoices = db.dish_invoiceDAO().getAllDishInvoices2();

            List<dish_invoice> userDishInvoices = new ArrayList<>();
            for (dish_invoice di : allDishInvoices) {
                if (userID.equals(di.getCustomerId()) && di.getInvoiceId() == null) {
                    userDishInvoices.add(di);
                }
            }

            Map<String, List<dish_cart>> restaurantCartMap = new HashMap<>();
            Map<String, restaurant> restaurantMap = new HashMap<>();
            Map<String, List<dish>> restaurantDishMap = new HashMap<>();

            for (dish_invoice di : userDishInvoices) {
                dish_cart cart = db.dish_cartDAO().getDishCartById2(di.getDishCartId());
                if (cart != null) {
                    dish d = db.dishDAO().getDishById2(cart.getDishId());
                    // Chỉ lấy món ăn chưa bị xóa
                    if (d != null && (d.getIsDeleted() == null || !d.getIsDeleted())) {
                        String resId = d.getRestaurantId();
                        restaurantCartMap.computeIfAbsent(resId, k -> new ArrayList<>()).add(cart);
                        restaurantMap.put(resId, db.restaurantDAO().getRestaurantById2(resId));
                        restaurantDishMap.computeIfAbsent(resId, k -> new ArrayList<>()).add(d);
                    }
                }
            }

            List<QuanGioHangAdapter.QuanGioHangItem> gioHangItems = new ArrayList<>();
            for (String resId : restaurantCartMap.keySet()) {
                gioHangItems.add(new QuanGioHangAdapter.QuanGioHangItem(
                        restaurantMap.get(resId),
                        restaurantCartMap.get(resId),
                        restaurantDishMap.get(resId)
                ));
            }

            runOnUiThread(() -> {
                adapterDonHangChuaDat = new QuanGioHangAdapter(gioHangItems, new QuanGioHangAdapter.OnCartCheckedChangeListener() {
                    @Override
                    public void onCartChecked(String restaurantId, String cartId, int price, boolean checked) {
                        int deliveryFee = 0;
                        String resDistrict = null;
                        if (restaurantMap.get(restaurantId) != null) {
                            resDistrict = restaurantMap.get(restaurantId).getDistrictName();
                        }

                        if (userDistrict == null || userDistrict.isEmpty()) {
                            Toast.makeText(CartActivity.this, "Vui lòng cập nhật vị trí để đặt hàng!", Toast.LENGTH_SHORT).show();
                            adapterDonHangChuaDat.uncheckCart(cartId);
                            return;
                        } else {
                            if (userDistrict.equalsIgnoreCase(resDistrict)) {
                                deliveryFee = 15000;
                            } else {
                                deliveryFee = 25000;
                            }
                        }

                        if (checked) {
                            if (selectedRestaurantId == null || selectedRestaurantId.equals(restaurantId)) {
                                selectedRestaurantId = restaurantId;
                                selectedCartIds.add(cartId);
                                tongTien += price;
                            } else {
                                Toast.makeText(CartActivity.this, "Chỉ được chọn món trong cùng một quán!", Toast.LENGTH_SHORT).show();
                                adapterDonHangChuaDat.uncheckCart(cartId);
                                return;
                            }
                        } else {
                            selectedCartIds.remove(cartId);
                            tongTien -= price;
                            if (selectedCartIds.isEmpty()) selectedRestaurantId = null;
                        }

                        tongCong = tongTien;
                        if (!selectedCartIds.isEmpty()) {
                            tongCong += deliveryFee;
                        }
                        tvTongCong.setText("Tổng cộng: " + tongCong + " VND");
                    }

                    @Override
                    public void onCartDeleted(String restaurantId, String cartId, int price, boolean wasChecked) {
                        // Nếu món bị xóa đang được chọn thì cập nhật lại tổng tiền và danh sách chọn
                        if (wasChecked) {
                            selectedCartIds.remove(cartId);
                            tongTien -= price;
                            if (selectedCartIds.isEmpty()) selectedRestaurantId = null;
                        }
                        // Tính lại tổng cộng (bao gồm phí vận chuyển nếu còn món được chọn)
                        int deliveryFee = 0;
                        String resDistrict = null;
                        if (restaurantMap.get(restaurantId) != null) {
                            resDistrict = restaurantMap.get(restaurantId).getDistrictName();
                        }
                        if (userDistrict != null && !userDistrict.isEmpty() && !selectedCartIds.isEmpty()) {
                            if (userDistrict.equalsIgnoreCase(resDistrict)) {
                                deliveryFee = 15000;
                            } else {
                                deliveryFee = 25000;
                            }
                        }
                        tongCong = tongTien;
                        if (!selectedCartIds.isEmpty()) {
                            tongCong += deliveryFee;
                        }
                        tvTongCong.setText("Tổng cộng: " + tongCong + " VND");
                    }
                });

                rvDonHangChuaDat.setLayoutManager(new LinearLayoutManager(this));
                rvDonHangChuaDat.setAdapter(adapterDonHangChuaDat);

                tvTongCong.setText("Tổng cộng: 0 VND");
                selectedCartIds.clear();
                selectedRestaurantId = null;
            });
        }).start();
    }

    private void setupNavigation() {
        findViewById(R.id.itemHome).setOnClickListener(v -> startActivity(new Intent(this, CustomerHomeActivity.class)));
        findViewById(R.id.itemSearch).setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        findViewById(R.id.itemCart).setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        findViewById(R.id.itemInfo).setOnClickListener(v -> startActivity(new Intent(this, CustomerInfoActivity.class)));
    }
}

