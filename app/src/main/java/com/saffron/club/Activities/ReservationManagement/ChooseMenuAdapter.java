package com.saffron.club.Activities.ReservationManagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.saffron.club.Models.MenuModel;
import com.saffron.club.Models.Table;
import com.saffron.club.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseMenuAdapter extends RecyclerView.Adapter<ChooseMenuAdapter.ViewHolder> {
    Context context;
    List<MenuModel> itemList;
    ChooseMenuCallback callback;

    public ChooseMenuAdapter(Context context, List<MenuModel> itemList, ChooseMenuCallback callback) {
        this.context = context;
        this.itemList = itemList;
        this.callback = callback;
    }

    public void setItemList(List<MenuModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item_layout, parent, false);
        ChooseMenuAdapter.ViewHolder viewHolder = new ChooseMenuAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MenuModel menu = itemList.get(position);
        holder.menuId.setText("Menu: " + menu.getId());
//        holder.bookTable.setText("Book " + menu.getName());
        holder.chooseMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onMenuChosen(menu);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView menuId;
        Button chooseMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            menuId = itemView.findViewById(R.id.menuId);
            chooseMenu = itemView.findViewById(R.id.chooseMenu);

        }
    }

    public interface ChooseMenuCallback {
        public void onMenuChosen(MenuModel menuModel);
    }
}
