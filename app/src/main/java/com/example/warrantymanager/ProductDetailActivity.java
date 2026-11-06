package com.example.warrantymanager;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private long id;
    private ImageView receipt;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_detail);
        SafeArea.apply(this);
        db = new DatabaseHelper(this);
        id = getIntent().getLongExtra("id", -1);
        receipt = findViewById(R.id.receiptImage);
        findViewById(R.id.edit).setOnClickListener(v -> {
            Intent i = new Intent(this, ProductFormActivity.class);
            i.putExtra("id", id);
            startActivity(i);
        });
        findViewById(R.id.delete).setOnClickListener(v -> new AlertDialog.Builder(this).setTitle("Delete product?").setMessage("This cannot be undone.").setNegativeButton("Cancel", null).setPositiveButton("Delete", (d, w) -> {
            db.delete(id);
            finish();
        }).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Product p = db.get(id);
        if (p == null) {
            finish();
            return;
        }
        ((TextView) findViewById(R.id.detailName)).setText(p.name);
        ((TextView) findViewById(R.id.detailInfo)).setText("Brand: " + p.brand + "\nCategory: " + p.category + "\nSeller: " + p.seller + "\nPurchase date: " + p.purchaseDate + "\nPrice: ₹" + p.price + "\nWarranty: " + p.warrantyMonths + " months\nExpiry: " + p.expiryDate + "\nStatus: " + Dates.status(p.expiryDate));
        receipt.setImageDrawable(null);
        if (p.receiptUri != null && !p.receiptUri.isEmpty()) {
            try {
                receipt.setImageURI(Uri.parse(p.receiptUri));
                receipt.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                receipt.setVisibility(View.GONE);
                Toast.makeText(this, "Receipt cannot be opened", Toast.LENGTH_LONG).show();
            }
        } else receipt.setVisibility(View.GONE);
    }
}
