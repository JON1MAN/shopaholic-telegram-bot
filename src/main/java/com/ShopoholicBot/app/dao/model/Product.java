package com.ShopoholicBot.app.dao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String url;
    private String name;
    private String image;

    @ManyToMany(mappedBy = "wishList")
    private List<User> users = new ArrayList<>();

    private double price;
    private double priceOnSale;
    private boolean isOnSale;
}
