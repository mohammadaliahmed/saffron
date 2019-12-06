package com.saffron.club.Activities.CartManagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.saffron.club.Models.MenuModel;
import com.saffron.club.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartMenuAdapter extends RecyclerView.Adapter<CartMenuAdapter.ViewHolder> {
    Context context;
    List<MenuModel> itemList = new ArrayList<>();
    CartCallbacks cartCallback;


    public CartMenuAdapter(Context context, List<MenuModel> itemList, CartCallbacks cartCallback) {
        this.context = context;
        this.itemList = itemList;
        this.cartCallback = cartCallback;
    }

    public void setItemList(List<MenuModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_layout, parent, false);
        CartMenuAdapter.ViewHolder viewHolder = new CartMenuAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final MenuModel menuModel = itemList.get(position);
        holder.title.setText((position + 1) + ") " + menuModel.getProduct().getName());
        holder.price.setText("$" + menuModel.getProduct().getPrice());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartCallback.onDeleteMenu(menuModel, position);
            }
        });
        final int[] count = {1};
        holder.quantity.setText("" + count[0]);

        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count[0] >= 0) {
                    count[0]++;
                    holder.quantity.setText("" + count[0]);
                    holder.price.setText("$" + count[0] * Double.parseDouble(menuModel.getProduct().getPrice()));
                    cartCallback.onIncrease(count[0],position);


                }
            }
        });
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count[0] > 0) {
                    count[0]--;
                    holder.quantity.setText("" + count[0]);
                    holder.price.setText("$" + count[0] * Double.parseDouble(menuModel.getProduct().getPrice()));
                    cartCallback.onDecrease(count[0],position);


                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView delete;
        TextView title, price, quantity;
        ImageView decrease, increase;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            quantity = itemView.findViewById(R.id.quantity);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            delete = itemView.findViewById(R.id.delete);
            decrease = itemView.findViewById(R.id.decrease);
            increase = itemView.findViewById(R.id.increase);
        }
    }

    public interface CartCallbacks {
        public void onDeleteMenu(MenuModel menu, int position);

        public void onIncrease(int value,int position);

        public void onDecrease(int value,int position);
    }
}
