package com.saffron.club.Activities.CartManagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.saffron.club.Models.BookingModel;
import com.saffron.club.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartTableAdapter extends RecyclerView.Adapter<CartTableAdapter.ViewHolder> {
    Context context;
    List<BookingModel> itemList = new ArrayList<>();
    CartTableCallbacks cartCallback;


    public CartTableAdapter(Context context, List<BookingModel> itemList, CartTableCallbacks cartCallback) {
        this.context = context;
        this.itemList = itemList;
        this.cartCallback = cartCallback;
    }

    public void setItemList(List<BookingModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_table_layout, parent, false);
        CartTableAdapter.ViewHolder viewHolder = new CartTableAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final BookingModel table = itemList.get(position);
        holder.title.setText((position + 1) + ") Table No#: " + table.getId()
                + "\n   Persons: " + table.getPersons() + "\n   Time: " + table.getTime() + "\n   Date: " + table.getDate());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartCallback.onDeleteTable(table, position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView delete;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            delete = itemView.findViewById(R.id.delete);

        }
    }

    public interface CartTableCallbacks {
        public void onDeleteTable(BookingModel table, int position);
    }
}
