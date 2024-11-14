package com.ShopoholicBot.app.service.user;

import com.ShopoholicBot.app.dao.model.Product;
import com.ShopoholicBot.app.dao.model.User;
import com.ShopoholicBot.app.dao.repository.product.ProductRepository;
import com.ShopoholicBot.app.dao.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {

    private UserRepository userRepository;
    private ProductRepository productRepository;

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void addItemToWishList(Long productId, Long userId) {
        Optional<User> user = findById(userId);
        Optional<Product> product = productRepository.findById(productId);
        user.ifPresent(user1 -> product.ifPresent(user1::addItem));
    }
}
