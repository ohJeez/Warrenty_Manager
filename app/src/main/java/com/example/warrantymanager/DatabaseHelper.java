package com.example.warrantymanager;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;

import java.util.*;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context c) {
        super(c, "warranty_vault.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE products (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,brand TEXT,category TEXT,purchase_date TEXT NOT NULL,price REAL NOT NULL,warranty_months INTEGER NOT NULL,expiry_date TEXT NOT NULL,seller TEXT,receipt_uri TEXT)");
        db.execSQL("CREATE TABLE notified (product_id INTEGER NOT NULL,days_before INTEGER NOT NULL,expiry_date TEXT NOT NULL,PRIMARY KEY(product_id,days_before,expiry_date))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
    }

    private ContentValues values(Product p) {
        ContentValues v = new ContentValues();
        v.put("name", p.name);
        v.put("brand", p.brand);
        v.put("category", p.category);
        v.put("purchase_date", p.purchaseDate);
        v.put("price", p.price);
        v.put("warranty_months", p.warrantyMonths);
        v.put("expiry_date", p.expiryDate);
        v.put("seller", p.seller);
        v.put("receipt_uri", p.receiptUri);
        return v;
    }

    public long add(Product p) {
        return getWritableDatabase().insertOrThrow("products", null, values(p));
    }

    public int update(Product p) {
        return getWritableDatabase().update("products", values(p), "id=?", new String[]{String.valueOf(p.id)});
    }

    public void delete(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("notified", "product_id=?", new String[]{String.valueOf(id)});
        db.delete("products", "id=?", new String[]{String.valueOf(id)});
    }

    private Product from(Cursor c) {
        return new Product(c.getLong(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getDouble(5), c.getInt(6), c.getString(7), c.getString(8), c.getString(9));
    }

    public Product get(long id) {
        try (Cursor c = getReadableDatabase().rawQuery("SELECT * FROM products WHERE id=?", new String[]{String.valueOf(id)})) {
            return c.moveToFirst() ? from(c) : null;
        }
    }

    public List<Product> all(String query, boolean expiring) {
        List<Product> out = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE (name LIKE ? OR brand LIKE ? OR category LIKE ?)" + (expiring ? " AND expiry_date>=date('now','localtime') AND expiry_date<=date('now','localtime','+30 days')" : "") + " ORDER BY expiry_date ASC";
        String q = "%" + query + "%";
        try (Cursor c = getReadableDatabase().rawQuery(sql, new String[]{q, q, q})) {
            while (c.moveToNext()) out.add(from(c));
        }
        return out;
    }

    public boolean alreadyNotified(long id, int days, String expiry) {
        try (Cursor c = getReadableDatabase().rawQuery("SELECT 1 FROM notified WHERE product_id=? AND days_before=? AND expiry_date=?", new String[]{String.valueOf(id), String.valueOf(days), expiry})) {
            return c.moveToFirst();
        }
    }

    public void markNotified(long id, int days, String expiry) {
        ContentValues v = new ContentValues();
        v.put("product_id", id);
        v.put("days_before", days);
        v.put("expiry_date", expiry);
        getWritableDatabase().insertWithOnConflict("notified", null, v, SQLiteDatabase.CONFLICT_IGNORE);
    }
}
