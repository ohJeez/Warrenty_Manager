package com.example.warrantymanager;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.activity.result.*;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

public class ProductFormActivity extends AppCompatActivity {
    private EditText name, brand, category, date, price, months, seller;
    private TextView receiptLabel;
    private String receiptUri = "";
    private long id = -1;
    private DatabaseHelper db;
    private final ActivityResultLauncher<Intent> picker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Uri uri = result.getData().getData();
            if (uri != null) {
                try {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException ignored) {
                }
                receiptUri = uri.toString();
                receiptLabel.setText("Receipt selected: " + uri.getLastPathSegment());
            }
        }
    });

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_form);
        SafeArea.apply(this);
        db = new DatabaseHelper(this);
        name = findViewById(R.id.name);
        brand = findViewById(R.id.brand);
        category = findViewById(R.id.category);
        date = findViewById(R.id.purchaseDate);
        price = findViewById(R.id.price);
        months = findViewById(R.id.months);
        seller = findViewById(R.id.seller);
        receiptLabel = findViewById(R.id.receiptLabel);
        id = getIntent().getLongExtra("id", -1);
        date.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            if (Dates.parse(date.getText().toString()) != null)
                c.setTime(Dates.parse(date.getText().toString()));
            new DatePickerDialog(this, (view, y, m, d) -> date.setText(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)), c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
        findViewById(R.id.pickReceipt).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            picker.launch(intent);
        });
        findViewById(R.id.save).setOnClickListener(v -> save());
        if (id != -1) {
            Product p = db.get(id);
            if (p != null) {
                name.setText(p.name);
                brand.setText(p.brand);
                category.setText(p.category);
                date.setText(p.purchaseDate);
                price.setText(String.valueOf(p.price));
                months.setText(String.valueOf(p.warrantyMonths));
                seller.setText(p.seller);
                receiptUri = p.receiptUri == null ? "" : p.receiptUri;
                receiptLabel.setText(receiptUri.isEmpty() ? "No receipt attached" : "Receipt attached");
            }
        } else date.setText(Dates.format(new Date()));
    }

    private void save() {
        String n = name.getText().toString().trim(), dt = date.getText().toString().trim(), mo = months.getText().toString().trim(), pr = price.getText().toString().trim();
        if (TextUtils.isEmpty(n)) {
            name.setError("Required");
            return;
        }
        if (Dates.parse(dt) == null) {
            date.setError("Select a valid date");
            return;
        }
        int m;
        double cost;
        try {
            m = Integer.parseInt(mo);
            cost = Double.parseDouble(pr);
            if (m < 1 || m > 240 || cost < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter valid price and warranty months (1–240)", Toast.LENGTH_LONG).show();
            return;
        }
        Product p = new Product(id, n, brand.getText().toString().trim(), category.getText().toString().trim(), dt, cost, m, Dates.expiry(dt, m), seller.getText().toString().trim(), receiptUri);
        if (id == -1) db.add(p);
        else db.update(p);
        Toast.makeText(this, "Product saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
