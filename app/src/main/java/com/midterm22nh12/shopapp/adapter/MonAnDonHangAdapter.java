package com.midterm22nh12.shopapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.model.entity.dish;
import com.midterm22nh12.shopapp.model.entity.dish_cart;
import java.util.List;

public class MonAnDonHangAdapter extends RecyclerView.Adapter<MonAnDonHangAdapter.ViewHolder> {
    private final List<dish_cart> cartList;
    private final List<dish> dishList;

    public MonAnDonHangAdapter(List<dish_cart> cartList, List<dish> dishList) {
        this.cartList = cartList;
        this.dishList = dishList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.monan_donhang, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        dish_cart cart = cartList.get(position);
        dish d = dishList.get(position);
        holder.tvName.setText(d.getName());
        holder.tvDescription.setText(d.getDescription());
        holder.tvPrice.setText(String.format("%,d₫", d.getPrice()));
        holder.tvQuantity.setText("Số lượng: " + cart.getQuantity());
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvPrice, tvQuantity;
        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_food_name);
            tvDescription = itemView.findViewById(R.id.tv_food_description);
            tvPrice = itemView.findViewById(R.id.tv_food_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
        }
    }
}
