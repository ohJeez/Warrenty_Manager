package com.example.warrantymanager;

import android.content.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.Holder> {
    private final List<Product> items = new ArrayList<>();
    private final Context context;

    public ProductAdapter(Context c) {
        context = c;
    }

    public void setItems(List<Product> products) {
        items.clear();
        items.addAll(products);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        Product p = items.get(position);
        h.name.setText(p.name);
        h.subtitle.setText(p.brand + " • " + p.category);
        h.status.setText(Dates.status(p.expiryDate));
        h.expiry.setText("Expires: " + p.expiryDate);
        h.status.setTextColor(Dates.daysLeft(p.expiryDate) < 0 ? Color.rgb(190, 40, 40) : Dates.daysLeft(p.expiryDate) <= 30 ? Color.rgb(180, 100, 0) : Color.rgb(0, 115, 83));
        h.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, ProductDetailActivity.class);
            i.putExtra("id", p.id);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView name, subtitle, status, expiry;

        Holder(View v) {
            super(v);
            name = v.findViewById(R.id.rowName);
            subtitle = v.findViewById(R.id.rowSubtitle);
            status = v.findViewById(R.id.rowStatus);
            expiry = v.findViewById(R.id.rowExpiry);
        }
    }
}
