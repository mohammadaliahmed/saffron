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
import com.saffron.club.Models.Category;
import com.saffron.club.Models.Product;
import com.saffron.club.R;
import com.saffron.club.Utils.AppConfig;
import com.saffron.club.Utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {
    Context context;
    List<Product> itemList = new ArrayList<>();
    List<Product> arrayList;
    AddToCartCallback cartCallback;
    ArrayList<String> productIdList = new ArrayList<>();

    public void setProductIdList(ArrayList<String> productIdList) {
        this.productIdList = productIdList;
        notifyDataSetChanged();
    }

    public ProductListAdapter(Context context, List<Product> itemList, ArrayList productIdList, AddToCartCallback cartCallback) {
        this.context = context;
        this.itemList = itemList;
        this.productIdList = productIdList;
        this.cartCallback = cartCallback;
        this.arrayList = new ArrayList<>(itemList);

    }

    public void updateList(List<Product> list) {
        this.itemList = list;
        arrayList.clear();
        arrayList.addAll(list);
        notifyDataSetChanged();
    }


    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        itemList.clear();
        if (charText.length() == 0) {
            itemList.addAll(arrayList);
        } else {
            for (Product text : arrayList) {
                if (text.getName().toLowerCase().contains(charText)
                        ) {
                    itemList.add(text);
                }
            }


        }
        notifyDataSetChanged();

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item_layout, parent, false);
        ProductListAdapter.ViewHolder viewHolder = new ProductListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Product product = itemList.get(position);
        boolean canAdd = true;
        if (productIdList.contains(product.getId())) {
            holder.addToCart.setText("Remove");
            holder.addToCart.setBackground(context.getResources().getDrawable(R.drawable.btn_bg_empty));
            holder.addToCart.setTextColor(context.getResources().getColor(R.color.colorSaffron));
            canAdd = false;
        } else {
            holder.addToCart.setText("Add");
            holder.addToCart.setBackground(context.getResources().getDrawable(R.drawable.btn_bg_green));
            holder.addToCart.setTextColor(context.getResources().getColor(R.color.colorWhite));


        }


        holder.title.setText(product.getName());
        holder.description.setText(product.getDesc());
        Glide.with(context).load(AppConfig.BASE_URL_Image + product.getImage()).into(holder.image);
        holder.price.setText("$ " + product.getPrice());
        final boolean finalCanAdd = canAdd;
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CommonUtils.showToast("Add to cart");
                if (finalCanAdd) {
                    cartCallback.onAddToCart(product);
                } else {
                    cartCallback.onRemoveFromCart(product);

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
        TextView title, price, description;
        Button addToCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            addToCart = itemView.findViewById(R.id.addToCart);
        }
    }

}
