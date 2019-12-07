package com.saffron.club.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.saffron.club.Models.Product;
import com.saffron.club.Models.Table;
import com.saffron.club.R;
import com.saffron.club.Utils.AppConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderTablesAdapter extends RecyclerView.Adapter<OrderTablesAdapter.ViewHolder> {
    Context context;
    List<Table> itemList = new ArrayList<>();


    public OrderTablesAdapter(Context context, List<Table> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setItemList(List<Table> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_table_item_layout, parent, false);
        OrderTablesAdapter.ViewHolder viewHolder = new OrderTablesAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Table table=itemList.get(position);

        holder.serial.setText((position+1)+"");
        holder.tableNumber.setText(""+table.getId());
        holder.time.setText(table.getTime());
        holder.date.setText(table.getDate());
        holder.persons.setText(table.getPersons());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView serial, tableNumber,time,date,persons;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serial = itemView.findViewById(R.id.serial);
            tableNumber = itemView.findViewById(R.id.tableNumber);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
            persons = itemView.findViewById(R.id.persons);
        }
    }


}
