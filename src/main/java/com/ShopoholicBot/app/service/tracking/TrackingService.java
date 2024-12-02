package com.ShopoholicBot.app.service.tracking;

import com.ShopoholicBot.app.dao.model.Bot;
import com.ShopoholicBot.app.dao.model.Product;
import com.ShopoholicBot.app.service.product.ProductService;
import com.ShopoholicBot.app.service.scraper.ScraperService;
import com.ShopoholicBot.app.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Component
@EnableScheduling
public class TrackingService {

    private ProductService productService;
    private ScraperService scraperService;

    @Scheduled(cron = "0 34 18 * * 1", zone = "Europe/Warsaw")
    @Async
    public void productTracking() {
        log.info("Tracking service is starting work...");

        List<Product> productsFromWishlist = productService.fetchAllProductsFromWishlist();
        log.info("Fetched products from wishlist, {}", (long) productsFromWishlist.size());

        List<Product> productsOnDiscount = fetchingDiscountProducts(productsFromWishlist);
        log.info("Fetched products on discount: {}", (long) productsOnDiscount.size());

        if (productsOnDiscount.isEmpty()) {
            log.info("There is no products on discount");
            return;
        }

        log.info("Creating map UserID - List<Product> ");
        var userProductMap = productsOnDiscount.stream()
                        .flatMap(product -> product.getUsers().stream()
                                .map(user -> Map.entry(user.getId(), product)))
                        .collect(Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                        ));

        log.info("Sending information of discounts to users");
        userProductMap.forEach((user, products) -> products.forEach(product -> Bot.sendInformationAboutDiscountProduct(user, product)));

        log.info("Tracking service is finishing work...");
    }

    private Optional<Product> checkForDiscount(String url) {
        final long delay = 13;
        log.info("Scraping for products from wishlist");
        try {
            Thread.sleep(delay * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return scraperService.scrapePage(url);
    }
    private List<Product> fetchingDiscountProducts(List<Product> products) {

        return products.stream()
                .map(product -> checkForDiscount(product.getUrl())
                        .orElseGet(() -> {
                            log.warn("Scraping failed or returned empty for product with id: {}", product.getId());
                            return null;
                        })
                )
                .filter(Objects::nonNull)
                .filter(scrapedProduct -> scrapedProduct.getPriceOnSale() <
                        products.stream()
                                .filter(p -> p.getUrl().equals(scrapedProduct.getUrl()))
                                .findFirst()
                                .map(Product::getPriceOnSale)
                                .orElse(scrapedProduct.getPriceOnSale())
                )
                .toList();
    }
}
