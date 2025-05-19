package com.midterm22nh12.shopapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.model.database.AppDatabase;
import com.midterm22nh12.shopapp.model.entity.dish;
import com.midterm22nh12.shopapp.model.entity.dish_cart;
import com.midterm22nh12.shopapp.model.entity.dish_invoice;
import java.util.HashSet;
import java.util.List;

public class MonAnGioHangAdapter extends RecyclerView.Adapter<MonAnGioHangAdapter.ViewHolder> {

    public interface OnCartCheckedChangeListener {
        void onCartChecked(String cartId, int price, boolean checked);
        void onCartDeleted(String cartId, int price, boolean wasChecked); // thêm callback xóa
    }

    private final List<dish_cart> cartList;
    private final List<dish> dishList;
    private final String restaurantId;
    private final OnCartCheckedChangeListener listener;
    private final HashSet<String> checkedCartIds = new HashSet<>();

    public MonAnGioHangAdapter(List<dish_cart> cartList, List<dish> dishList, String restaurantId, OnCartCheckedChangeListener listener) {
        this.cartList = cartList;
        this.dishList = dishList;
        this.restaurantId = restaurantId;
        this.listener = listener;
    }

    public void uncheckCart(String cartId) {
        int idx = -1;
        for (int i = 0; i < cartList.size(); i++) {
            if (cartList.get(i).getId().equals(cartId)) {
                idx = i;
                break;
            }
        }
        if (idx != -1) {
            cartList.get(idx).setSelected(false);
            notifyItemChanged(idx);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.monan_dathang, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        dish_cart cart = cartList.get(position);
        dish d = dishList.get(position);
        holder.tvName.setText(d.getName());
        holder.tvDescription.setText(d.getDescription());
        holder.tvPrice.setText(String.format("%,d₫", d.getPrice()));
        holder.editQuantity.setText(String.valueOf(cart.getQuantity()));
        holder.checkbox.setOnCheckedChangeListener(null);
        holder.checkbox.setChecked(cart.isSelected());
        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cart.setSelected(isChecked);
            if (listener != null) {
                listener.onCartChecked(cart.getId(), d.getPrice() * cart.getQuantity(), isChecked);
            }
        });

        // Xử lý nút tăng số lượng
        holder.btnIncrease.setOnClickListener(v -> {
            int qty = cart.getQuantity();
            qty++;
            cart.setQuantity(qty);
            holder.editQuantity.setText(String.valueOf(qty));
            // Cập nhật DB
            updateCartQuantity(holder.itemView.getContext(), cart.getId(), qty);
            // Nếu đang chọn, cập nhật lại tổng tiền
            if (cart.isSelected() && listener != null) {
                listener.onCartChecked(cart.getId(), d.getPrice(), true);
            }
        });

        // Xử lý nút giảm số lượng
        holder.btnDecrease.setOnClickListener(v -> {
            int qty = cart.getQuantity();
            if (qty > 1) {
                qty--;
                cart.setQuantity(qty);
                holder.editQuantity.setText(String.valueOf(qty));
                // Cập nhật DB
                updateCartQuantity(holder.itemView.getContext(), cart.getId(), qty);
                // Nếu đang chọn, cập nhật lại tổng tiền
                if (cart.isSelected() && listener != null) {
                    listener.onCartChecked(cart.getId(), -d.getPrice(), true);
                }
            }
        });

        // Xử lý thay đổi trực tiếp số lượng qua EditText
        holder.editQuantity.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = holder.editQuantity.getText().toString().trim();
                int qty = 1;
                try {
                    qty = Integer.parseInt(text);
                    if (qty < 1) qty = 1;
                } catch (Exception ignored) {}
                if (qty != cart.getQuantity()) {
                    int diff = qty - cart.getQuantity();
                    cart.setQuantity(qty);
                    holder.editQuantity.setText(String.valueOf(qty));
                    updateCartQuantity(holder.itemView.getContext(), cart.getId(), qty);
                    if (cart.isSelected() && listener != null) {
                        listener.onCartChecked(cart.getId(), d.getPrice() * diff, true);
                    }
                }
            }
        });

        // Xử lý nút xóa
        holder.btnDelete.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            new AlertDialog.Builder(context)
                .setTitle("Xóa món khỏi giỏ hàng")
                .setMessage("Bạn có chắc muốn xóa món này khỏi giỏ hàng?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Xóa dish_cart và dish_invoice khỏi DB
                    new Thread(() -> {
                        AppDatabase db = AppDatabase.getInstance(context);
                        db.dish_cartDAO().deleteById(cart.getId());
                        db.dish_invoiceDAO().deleteByDishCartId(cart.getId());
                        boolean wasChecked = cart.isSelected();
                        int price = d.getPrice() * cart.getQuantity();
                        // Cập nhật UI
                        new Handler(Looper.getMainLooper()).post(() -> {
                            int idx = holder.getAdapterPosition();
                            if (idx != RecyclerView.NO_POSITION) {
                                cartList.remove(idx);
                                dishList.remove(idx);
                                notifyItemRemoved(idx);
                                Toast.makeText(context, "Đã xóa món khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                                // Gọi callback cập nhật tổng tiền
                                if (listener != null) {
                                    listener.onCartDeleted(cart.getId(), price, wasChecked);
                                }
                            }
                        });
                    }).start();
                })
                .setNegativeButton("Hủy", null)
                .show();
        });
    }

    private void updateCartQuantity(Context context, String cartId, int quantity) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            db.dish_cartDAO().updateQuantity(cartId, quantity);
        }).start();
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvPrice, tvQuantity;
        EditText editQuantity;
        CheckBox checkbox;
        ImageButton btnDelete;
        Button btnIncrease, btnDecrease;
        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_food_name);
            tvDescription = itemView.findViewById(R.id.tv_food_description);
            tvPrice = itemView.findViewById(R.id.tv_food_price);
            editQuantity = itemView.findViewById(R.id.edit_quantity);
            checkbox = itemView.findViewById(R.id.checkbox_select);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
        }
    }
}
