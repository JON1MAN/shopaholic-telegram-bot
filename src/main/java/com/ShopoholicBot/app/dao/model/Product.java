package com.ShopoholicBot.app.dao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private double price;
    private double priceOnSale;
    private boolean isOnSale;
}
