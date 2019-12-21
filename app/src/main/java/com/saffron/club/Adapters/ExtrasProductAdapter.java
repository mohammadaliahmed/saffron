package com.saffron.club.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.saffron.club.Activities.Callbacks.AddToCartCallback;
import com.saffron.club.Models.Product;
import com.saffron.club.R;
import com.saffron.club.Utils.AppConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExtrasProductAdapter extends RecyclerView.Adapter<ExtrasProductAdapter.ViewHolder> {
    Context context;
    List<Product> itemList = new ArrayList<>();
    AddExtraCallback callback;

    HashMap<Integer, Integer> addedMap = new HashMap<>();


    public ExtrasProductAdapter(Context context, List<Product> itemList, AddExtraCallback callback) {
        this.context = context;
        this.itemList = itemList;
        this.callback = callback;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.extra_item_layout, parent, false);
        ExtrasProductAdapter.ViewHolder viewHolder = new ExtrasProductAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Product product = itemList.get(position);
        boolean canAdd = true;
        if (addedMap.containsKey(product.getId())) {
            holder.addToCart.setText("Remove");
            holder.addToCart.setBackground(context.getResources().getDrawable(R.drawable.btn_bg_empty));
            holder.addToCart.setTextColor(context.getResources().getColor(R.color.colorSaffron));
            canAdd = false;
        } else {
            holder.addToCart.setText("Add");
            holder.addToCart.setBackground(context.getResources().getDrawable(R.drawable.btn_bg));
            holder.addToCart.setTextColor(context.getResources().getColor(R.color.colorWhite));
        }

        holder.title.setText(product.getName());
        Glide.with(context).load(AppConfig.BASE_URL_Image + product.getImage()).into(holder.image);
        holder.price.setText("$ " + product.getPrice());
        final boolean finalCanAdd = canAdd;
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalCanAdd) {
                    callback.onAdd(product);
                    addedMap.put(product.getId(), product.getId());
                    notifyDataSetChanged();
                } else {
                    callback.onRemove(product);
                    addedMap.remove(product.getId());
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, price;
        Button addToCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            addToCart = itemView.findViewById(R.id.addToCart);
        }
    }

    public interface AddExtraCallback {
        public void onAdd(Product product);

        public void onRemove(Product product);
    }

}
