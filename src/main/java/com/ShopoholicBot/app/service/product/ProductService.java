package com.ShopoholicBot.app.service.product;

import com.ShopoholicBot.app.dao.model.Product;
import com.ShopoholicBot.app.dao.repository.product.ProductRepository;
import com.ShopoholicBot.app.dao.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ProductService {


    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    public Optional<Product> findById(Long id) {
        log.info("Fetched product with id: {}", id);
        return productRepository.findById(id);
    }

    public Product create(Product product) {
        log.info("Creating product: {}", product);
        return productRepository.save(product);
    }

    public List<Product> fetchAllProductsFromWishlist() {
        log.info("Fetching all products from wishlist");
        return productRepository.selectAllProductsFromWishlist();
    }
}
