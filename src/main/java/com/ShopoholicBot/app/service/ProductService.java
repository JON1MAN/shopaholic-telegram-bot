package com.ShopoholicBot.app.service;

import com.ShopoholicBot.app.dao.model.Product;
import com.ShopoholicBot.app.dao.repository.product.ProductRepository;
import com.ShopoholicBot.app.dao.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
