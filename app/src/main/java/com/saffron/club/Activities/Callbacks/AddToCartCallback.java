package com.saffron.club.Activities.Callbacks;

import com.saffron.club.Models.MenuModel;
import com.saffron.club.Models.Product;

public interface AddToCartCallback {
    public void onAddToCart(Product product);
    public void onRemoveFromCart(Product product);
}
