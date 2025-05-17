package com.midterm22nh12.shopapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm22nh12.shopapp.R;
import com.midterm22nh12.shopapp.model.entity.dish;

import java.util.List;

public class DishUserAdapter extends RecyclerView.Adapter<DishUserAdapter.DishViewHolder> {

    private List<dish> dishList;
    private OnDishEditListener editListener;
    private OnDishDeleteListener deleteListener;

    public interface OnDishEditListener {
        void onEdit(dish dish);
    }

    public interface OnDishDeleteListener {
        void onDelete(dish dish);
    }

    public DishUserAdapter(List<dish> dishList, OnDishEditListener editListener, OnDishDeleteListener deleteListener) {
        this.dishList = dishList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.monan_chu_item, parent, false);
        Log.d("DishUserAdapter", "Creating view holder");
        return new DishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        Log.d("DishUserAdapter", "Binding view holder at position: " + position);
        dish currentDish = dishList.get(position);
        Log.d("DishUserAdapter", "Dish: " + (currentDish != null ? currentDish.toString() : "null"));
        try {
            if (currentDish != null) {
                holder.tvFoodName.setText(currentDish.getName() != null ? currentDish.getName() : "Không có tên");
                holder.tvFoodDescription.setText(currentDish.getDescription() != null ? currentDish.getDescription() : "Không có mô tả");
                holder.tvFoodPrice.setText(String.format("Giá: %,d VND", currentDish.getPrice() != 0 ? currentDish.getPrice() : 0));
                if (currentDish.getImage() != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(currentDish.getImage(), 0, currentDish.getImage().length);
                    holder.imgFood.setImageBitmap(bitmap);
                } else {
                    holder.imgFood.setImageResource(R.drawable.ic_food_placeholder);
                }
            }
        } catch (Exception e) {
            Log.e("DishUserAdapter", "Error in onBindViewHolder: " + e.getMessage());
        }
        holder.btnEdit.setOnClickListener(v -> editListener.onEdit(currentDish));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(currentDish));
    }

    @Override
    public int getItemCount() {
        Log.d("DishUserAdapter", "Item count: " + dishList.size());
        return dishList.size();
    }

    public static class DishViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgFood;
        private final TextView tvFoodName, tvFoodDescription, tvFoodPrice;
        private final Button btnEdit, btnDelete;

        public DishViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodDescription = itemView.findViewById(R.id.tvFoodDescription);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public void updateDishes(List<dish> newDishes) {
        this.dishList = newDishes;
        Log.d("DishUserAdapter", "Updating dishes: " + newDishes.size());
        notifyDataSetChanged();
    }
}
