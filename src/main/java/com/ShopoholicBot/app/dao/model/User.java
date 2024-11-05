package com.ShopoholicBot.app.dao.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

}
