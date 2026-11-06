package com.example.warrantymanager;

public class Product {
    public long id;
    public String name, brand, category, purchaseDate, expiryDate, seller, receiptUri;
    public double price;
    public int warrantyMonths;

    public Product(long id, String name, String brand, String category, String purchaseDate, double price, int warrantyMonths, String expiryDate, String seller, String receiptUri) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.purchaseDate = purchaseDate;
        this.price = price;
        this.warrantyMonths = warrantyMonths;
        this.expiryDate = expiryDate;
        this.seller = seller;
        this.receiptUri = receiptUri;
    }
}
