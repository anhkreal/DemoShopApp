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
import androidx.recyclerview.widget.RecyclerView;
import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.model.entity.dish;
import com.midterm22nh12.shopapp.model.entity.dish_cart;
import com.midterm22nh12.shopapp.model.entity.invoice;
import com.midterm22nh12.shopapp.model.entity.restaurant;
import java.util.List;

public class QuanDonHangAdapter extends RecyclerView.Adapter<QuanDonHangAdapter.ViewHolder> {

    public static class QuanDonHangItem {
        public final restaurant res;
        public final invoice inv;
        public final List<dish_cart> carts;
        public final List<dish> dishes;
        public QuanDonHangItem(restaurant res, invoice inv, List<dish_cart> carts, List<dish> dishes) {
            this.res = res;
            this.inv = inv;
            this.carts = carts;
            this.dishes = dishes;
        }
    }

    public interface OnOrderChangedListener {
        void onOrderChanged();
    }

    private final List<QuanDonHangItem> items;
    private final OnOrderChangedListener orderChangedListener;

    public QuanDonHangAdapter(List<QuanDonHangItem> items, OnOrderChangedListener listener) {
        this.items = items;
        this.orderChangedListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quan_donhang_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuanDonHangItem item = items.get(position);
        holder.tvTenNhaHang.setText(item.res != null ? item.res.getName() : "Nhà hàng");
        int tongTien = (item.inv != null) ? item.inv.getTotalPayment() : 0;
        holder.tvTongTien.setText("Tổng: " + tongTien + " VND");
        holder.recyclerMonAn.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerMonAn.setAdapter(new MonAnDonHangAdapter(item.carts, item.dishes));

        holder.btnXacNhanThanhToan.setOnClickListener(v -> {
            if (item.inv == null) return;
            String status = item.inv.getStatus();
            if ("Chờ xác nhận".equals(status)) {
                android.widget.Toast.makeText(
                    holder.itemView.getContext(),
                    "Nhà hàng chưa xác nhận đơn hàng!",
                    android.widget.Toast.LENGTH_SHORT
                ).show();
            } else if ("Chờ nhận hàng".equals(status)) {
                item.inv.setStatus("Hoàn tất");
                new Thread(() -> {
                    AppDatabase db = AppDatabase.getInstance(holder.itemView.getContext());
                    db.invoiceDAO().updateInvoice(item.inv);
                    holder.itemView.post(() -> {
                        android.widget.Toast.makeText(
                            holder.itemView.getContext(),
                            "Đã xác nhận hoàn tất đơn hàng!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show();
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
        Button btnXacNhanThanhToan;
        ViewHolder(View itemView) {
            super(itemView);
            tvTenNhaHang = itemView.findViewById(R.id.tv_ten_nha_hang);
            tvTongTien = itemView.findViewById(R.id.tv_tong_tien);
            recyclerMonAn = itemView.findViewById(R.id.recycler_monan_donhang);
            btnXacNhanThanhToan = itemView.findViewById(R.id.btn_xacnhan_thanhtoan);
        }
    }
}
