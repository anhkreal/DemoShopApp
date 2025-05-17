package com.midterm22nh12.shopapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.model.entity.dish;
import com.midterm22nh12.shopapp.model.entity.restaurant;
import com.midterm22nh12.shopapp.view.DishDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class MonanAdapter extends RecyclerView.Adapter<MonanAdapter.ViewHolder> {
    private final List<dish> dishes;

    public MonanAdapter(List<dish> dishes) {
        this.dishes = (dishes != null) ? dishes : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.monan_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        dish currentDish = dishes.get(position);
        holder.foodName.setText(currentDish.getName());
        holder.foodDescription.setText(currentDish.getDescription());
        holder.foodPrice.setText(String.format("%d₫", currentDish.getPrice()));

        // Convert byte[] to Bitmap and set it to the ImageView
        byte[] imageBytes = currentDish.getImage();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.foodImage.setImageBitmap(bitmap);
        } else {
            holder.foodImage.setImageResource(R.drawable.ic_placeholder); // Default placeholder
        }

        holder.btnDetails.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, DishDetailActivity.class);
            intent.putExtra("dishID", currentDish.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodDescription, foodPrice;
        ImageView foodImage;
        View btnDetails; // Reference to the "Chi tiết" button

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.food_name);
            foodDescription = itemView.findViewById(R.id.food_description);
            foodPrice = itemView.findViewById(R.id.food_price);
            foodImage = itemView.findViewById(R.id.food_image); // Reference to the ImageView
            btnDetails = itemView.findViewById(R.id.btn_details);
        }
    }
}
