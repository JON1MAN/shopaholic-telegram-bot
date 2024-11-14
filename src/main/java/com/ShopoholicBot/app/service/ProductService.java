package com.ShopoholicBot.app.service;

import com.ShopoholicBot.app.dao.model.Product;
import com.ShopoholicBot.app.dao.repository.product.ProductRepository;
import com.ShopoholicBot.app.dao.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class ProductService {


    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }
}
