package com.midterm22nh12.shopapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.model.entity.restaurant;
import com.midterm22nh12.shopapp.view.RestaurantDetailActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

public class Quan1Adapter extends RecyclerView.Adapter<Quan1Adapter.ViewHolder> {
    private final List<restaurant> restaurants;
    private final String userDistrict;

    // Thêm userDistrict vào constructor
    public Quan1Adapter(List<restaurant> restaurants, String userDistrict) {
        this.restaurants = (restaurants != null) ? restaurants : new ArrayList<>();
        this.userDistrict = userDistrict;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quan1_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        restaurant currentRestaurant = restaurants.get(position);
        holder.shopName.setText(currentRestaurant.getName());
        holder.shopAddress.setText(currentRestaurant.getStreetName() + ", " + currentRestaurant.getDistrictName());

        // Xử lý thời gian và phí vận chuyển
        int deliveryTime = 0;
        int deliveryFee = 0;
        if (userDistrict != null && !userDistrict.isEmpty()) {
            if (userDistrict.equalsIgnoreCase(currentRestaurant.getDistrictName())) {
                deliveryTime = 15;
                deliveryFee = 15000;
            } else {
                deliveryTime = 30;
                deliveryFee = 25000;
            }
        }
        holder.deliveryInfo.setText(String.format("Thời gian giao hàng: %d phút, phí %d₫", deliveryTime, deliveryFee));

        // Convert byte[] to Bitmap and set it to the ImageView
        byte[] imageBytes = currentRestaurant.getImageData();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.shopImage.setImageBitmap(bitmap);
        } else {
            holder.shopImage.setImageResource(R.drawable.ic_placeholder); // Default placeholder
        }

        holder.btnDetails.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, RestaurantDetailActivity.class);
            intent.putExtra("restaurantID", currentRestaurant.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView shopName, shopAddress, deliveryInfo;
        ImageView shopImage;
        View btnDetails; // Reference to the "Chi tiết" button

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            shopName = itemView.findViewById(R.id.shop_name);
            shopAddress = itemView.findViewById(R.id.shop_address);
            deliveryInfo = itemView.findViewById(R.id.delivery_info);
            shopImage = itemView.findViewById(R.id.item_image); // Reference to the ImageView
            btnDetails = itemView.findViewById(R.id.btn_details);
        }
    }
}
