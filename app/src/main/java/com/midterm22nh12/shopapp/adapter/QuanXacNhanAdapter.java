package com.midterm22nh12.shopapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.model.entity.dish;
import com.midterm22nh12.shopapp.model.entity.dish_cart;
import com.midterm22nh12.shopapp.model.entity.invoice;
import com.midterm22nh12.shopapp.model.entity.restaurant;
import java.util.List;

public class QuanXacNhanAdapter extends RecyclerView.Adapter<QuanXacNhanAdapter.ViewHolder> {

    public static class QuanXacNhanItem {
        public final restaurant res;
        public final invoice inv;
        public final List<dish_cart> carts;
        public final List<dish> dishes;
        public QuanXacNhanItem(restaurant res, invoice inv, List<dish_cart> carts, List<dish> dishes) {
            this.res = res;
            this.inv = inv;
            this.carts = carts;
            this.dishes = dishes;
        }
    }

    public interface OnOrderChangedListener {
        void onOrderChanged();
    }

    private final List<QuanXacNhanItem> items;
    private final OnOrderChangedListener orderChangedListener;

    public QuanXacNhanAdapter(List<QuanXacNhanItem> items, OnOrderChangedListener listener) {
        this.items = items;
        this.orderChangedListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quan_xacnhan_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuanXacNhanItem item = items.get(position);
        holder.tvTenNhaHang.setText(item.res != null ? item.res.getName() : "Nhà hàng");
        int tongTien = (item.inv != null) ? item.inv.getTotalPayment() : 0;
        holder.tvTongTien.setText("Tổng: " + tongTien + " VND");
        holder.recyclerMonAn.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerMonAn.setAdapter(new MonAnDonHangAdapter(item.carts, item.dishes));

        holder.btnXacNhanDonHang.setOnClickListener(v -> {
            if (item.inv == null) return;
            String status = item.inv.getStatus();
            if ("Chờ xác nhận".equals(status)) {
                item.inv.setStatus("Chờ nhận hàng");
                new Thread(() -> {
                    AppDatabase db = AppDatabase.getInstance(holder.itemView.getContext());
                    db.invoiceDAO().updateInvoice(item.inv);
                    holder.itemView.post(() -> {
                        android.widget.Toast.makeText(
                            holder.itemView.getContext(),
                            "Đã xác nhận đơn hàng!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show();
                        // Gọi callback reload lại trang
                        if (orderChangedListener != null) {
                            orderChangedListener.onOrderChanged();
                        }
                    });
                }).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenNhaHang, tvTongTien;
        RecyclerView recyclerMonAn;
        Button btnXacNhanDonHang;
        ViewHolder(View itemView) {
            super(itemView);
            tvTenNhaHang = itemView.findViewById(R.id.tv_ten_nha_hang);
            tvTongTien = itemView.findViewById(R.id.tv_tong_tien);
            recyclerMonAn = itemView.findViewById(R.id.recycler_monan_donhang);
            btnXacNhanDonHang = itemView.findViewById(R.id.btn_xacnhan_donhang);
        }
    }
}
