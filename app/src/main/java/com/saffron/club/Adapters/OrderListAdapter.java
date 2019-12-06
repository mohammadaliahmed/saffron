package com.saffron.club.Adapters;

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
import com.saffron.club.Models.OrderModel;
import com.saffron.club.R;
import com.saffron.club.Utils.AppConfig;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {
    Context context;
    List<OrderModel> itemList = new ArrayList<>();

    public OrderListAdapter(Context context, List<OrderModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setItemList(List<OrderModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_layout, parent, false);
        OrderListAdapter.ViewHolder viewHolder = new OrderListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel orderModel = itemList.get(position);

        holder.orderId.setText((position + 1) + ")   Order Id: " + orderModel.getId() + "\nProducts: " + orderModel.getProducts()
                + "\nTotal: " + orderModel.getTotal()
        );

        String[] date = orderModel.getCreatedAt().split(" ");
        holder.date.setText(date[1] + "\n" + date[0]);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderId);
            date = itemView.findViewById(R.id.date);
        }
    }
}
