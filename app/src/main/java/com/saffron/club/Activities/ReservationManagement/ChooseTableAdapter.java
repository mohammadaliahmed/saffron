package com.saffron.club.Activities.ReservationManagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.saffron.club.Models.Table;
import com.saffron.club.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseTableAdapter extends RecyclerView.Adapter<ChooseTableAdapter.ViewHolder> {
    Context context;
    List<Table> itemList;
    ChooseTableCallback callback;
    ArrayList<String> productIdList = new ArrayList<>();


    public void setProductIdList(ArrayList<String> productIdList) {
        this.productIdList = productIdList;
        notifyDataSetChanged();
    }

    public ChooseTableAdapter(Context context, List<Table> itemList, ChooseTableCallback callback) {
        this.context = context;
        this.itemList = itemList;
        this.callback = callback;
    }

    public void setItemList(List<Table> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.table_item_layout, parent, false);
        ChooseTableAdapter.ViewHolder viewHolder = new ChooseTableAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Table table = itemList.get(position);

        boolean canAdd = true;
        if (productIdList.contains(table.getId())) {
            holder.bookTable.setText("Remove");
            holder.bookTable.setBackground(context.getResources().getDrawable(R.drawable.btn_bg_empty));
            holder.bookTable.setTextColor(context.getResources().getColor(R.color.colorSaffron));
            canAdd = false;
        } else {
            holder.bookTable.setText("Add");
            holder.bookTable.setBackground(context.getResources().getDrawable(R.drawable.btn_bg_green));
            holder.bookTable.setTextColor(context.getResources().getColor(R.color.colorWhite));


        }

        final boolean finalCanAdd = canAdd;

        holder.tableId.setText("Table: " + table.getId() + "\nPersons: " + table.getPersons());
//        holder.bookTable.setText("Book " + table.getName());
        holder.bookTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalCanAdd) {
                    callback.onTableChosen(table);
                } else {
                    callback.onTableRemove(table);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tableId;
        Button bookTable;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tableId = itemView.findViewById(R.id.tableId);
            bookTable = itemView.findViewById(R.id.bookTable);

        }
    }

    public interface ChooseTableCallback {
        public void onTableChosen(Table table);

        public void onTableRemove(Table table);
    }
}
