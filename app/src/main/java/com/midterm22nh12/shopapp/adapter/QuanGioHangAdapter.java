package com.midterm22nh12.shopapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.model.entity.dish;
import com.midterm22nh12.shopapp.model.entity.dish_cart;
import com.midterm22nh12.shopapp.model.entity.restaurant;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class QuanGioHangAdapter extends RecyclerView.Adapter<QuanGioHangAdapter.ViewHolder> {

    public static class QuanGioHangItem {
        public final restaurant res;
        public final List<dish_cart> carts;
        public final List<dish> dishes;
        public QuanGioHangItem(restaurant res, List<dish_cart> carts, List<dish> dishes) {
            this.res = res;
            this.carts = carts;
            this.dishes = dishes;
        }
    }

    public interface OnCartCheckedChangeListener {
        void onCartChecked(String restaurantId, String cartId, int price, boolean checked);
    }

    private final List<QuanGioHangItem> items;
    private final OnCartCheckedChangeListener listener;
    private final Map<String, MonAnGioHangAdapter> adapterMap = new HashMap<>();

    public QuanGioHangAdapter(List<QuanGioHangItem> items, OnCartCheckedChangeListener listener) {
        this.items = items;
        this.listener = listener;
    }

    // Cho phép CartActivity gọi để bỏ chọn checkbox ở quán khác
    public void uncheckCart(String cartId) {
        for (MonAnGioHangAdapter adapter : adapterMap.values()) {
            adapter.uncheckCart(cartId);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quan_giohang_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuanGioHangItem item = items.get(position);
        holder.tvTenNhaHang.setText(item.res != null ? item.res.getName() : "Nhà hàng");
        MonAnGioHangAdapter adapter = new MonAnGioHangAdapter(
                item.carts,
                item.dishes,
                item.res != null ? item.res.getId() : "",
                (cartId, price, checked) -> {
                    if (listener != null) {
                        listener.onCartChecked(item.res != null ? item.res.getId() : "", cartId, price, checked);
                    }
                }
        );
        adapterMap.put(item.res != null ? item.res.getId() : "", adapter);
        holder.recyclerMonAn.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerMonAn.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenNhaHang;
        RecyclerView recyclerMonAn;
        ViewHolder(View itemView) {
            super(itemView);
            tvTenNhaHang = itemView.findViewById(R.id.tv_ten_nha_hang);
            recyclerMonAn = itemView.findViewById(R.id.recycler_monan_dathang);
        }
    }
}
