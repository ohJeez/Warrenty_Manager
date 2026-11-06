package com.example.warrantymanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ProductAdapter adapter;

    private EditText search;
    private TextView empty;

    // Dashboard statistic cards
    private TextView totalCount;
    private TextView activeCount;
    private TextView soonCount;
    private TextView expiredCount;

    private boolean expiring = false;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.activity_main);
        SafeArea.apply(this);

        // Initialize database
        db = new DatabaseHelper(this);

        // Initialize product list
        adapter = new ProductAdapter(this);

        RecyclerView list = findViewById(R.id.productList);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        // Initialize search and empty state
        search = findViewById(R.id.search);
        empty = findViewById(R.id.empty);

        // Initialize dashboard cards
        totalCount = findViewById(R.id.totalCount);
        activeCount = findViewById(R.id.activeCount);
        soonCount = findViewById(R.id.soonCount);
        expiredCount = findViewById(R.id.expiredCount);

        // Add product
        findViewById(R.id.addButton).setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ProductFormActivity.class
            );
            startActivity(intent);
        });

        // Show all products
        findViewById(R.id.allButton).setOnClickListener(v -> {
            expiring = false;
            refresh();
        });

        // Show products expiring soon
        findViewById(R.id.expiringButton).setOnClickListener(v -> {
            expiring = true;
            refresh();
        });

        // Search products
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {
                refresh();
            }

            @Override
            public void afterTextChanged(Editable e) {
            }
        });

        // Warranty expiry notifications
        ReminderWorker.channel(this);
        ReminderWorker.schedule(this);

        // Request notification permission
        if (Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(
                        Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.POST_NOTIFICATIONS
                    },
                    101
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {

        // Fetch all products
        List<Product> all = db.all("", false);

        int active = 0;
        int soon = 0;
        int expired = 0;

        // Calculate warranty statistics
        for (Product p : all) {

            long days = Dates.daysLeft(p.expiryDate);

            if (days < 0) {
                expired++;
            } else if (days <= 30) {
                soon++;
            } else {
                active++;
            }
        }

        // Update dashboard cards
        totalCount.setText(
                all.size() + "\nTotal products"
        );

        activeCount.setText(
                active + "\nActive warranties"
        );

        soonCount.setText(
                soon + "\nExpiring soon"
        );

        expiredCount.setText(
                expired + "\nExpired"
        );

        // Fetch filtered products
        List<Product> items = db.all(
                search.getText().toString().trim(),
                expiring
        );

        // Update RecyclerView
        adapter.setItems(items);

        // Display empty state when necessary
        empty.setVisibility(
                items.isEmpty() ? View.VISIBLE : View.GONE
        );
    }
}