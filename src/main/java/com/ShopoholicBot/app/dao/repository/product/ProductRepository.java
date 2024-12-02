package com.ShopoholicBot.app.dao.repository.product;

import com.ShopoholicBot.app.dao.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    @Query(value = """
    SELECT p
    FROM User u
    INNER JOIN u.wishList p
    """)
    List<Product> selectAllProductsFromWishlist();

}
