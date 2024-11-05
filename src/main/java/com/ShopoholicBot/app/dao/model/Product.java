package com.ShopoholicBot.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private String url;
    private String name;
    private String image;
    private double price;
    private double priceOnSale;
    private boolean isOnSale;
}
