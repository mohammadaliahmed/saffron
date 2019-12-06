package com.saffron.club.Activities.ReservationManagement;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.saffron.club.Activities.ListOfProducts;
import com.saffron.club.Models.Category;
import com.saffron.club.R;
import com.saffron.club.Utils.AppConfig;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MenuFragmentCategoryAdapter extends RecyclerView.Adapter<MenuFragmentCategoryAdapter.ViewHolder> {
    Context context;
    List<Category> itemList = new ArrayList<>();
    MenuFragmentCategoryCallbacks categoryCallbacks;
    public MenuFragmentCategoryAdapter(Context context, List<Category> itemList,MenuFragmentCategoryCallbacks categoryCallbacks) {
        this.context = context;
        this.itemList = itemList;
        this.categoryCallbacks = categoryCallbacks;
    }

    public void setItemList(List<Category> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_category_item, parent, false);
        MenuFragmentCategoryAdapter.ViewHolder viewHolder = new MenuFragmentCategoryAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Category category = itemList.get(position);
        holder.title.setText(category.getName());
        Glide.with(context).load(AppConfig.BASE_URL + "storage/app/" + category.getImage()).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              categoryCallbacks.onOptionChosen(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
        }
    }
    public interface MenuFragmentCategoryCallbacks{
        public void onOptionChosen(Category category);
    }
}
