package com.example.top.common.pojo;

import com.google.firebase.database.Exclude;

import java.util.Objects;

public class Product {

    public static String ID = "id";
    public static String NAME = "name";
    public static String QUANTITY = "quantity";
    public static String PHOTO_URL = "photoUrl";

    @Exclude
    private String id;
    private String name;
    private int quantity;
    private String photoUrl;

    public Product() {
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
