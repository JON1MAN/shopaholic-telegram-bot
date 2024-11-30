package com.ShopoholicBot.app.service.scraper;

import java.net.URI;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.ShopoholicBot.app.dao.model.Product;
import com.ShopoholicBot.app.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScraperService {

    @Value("${diffbot.DIFFBOT_API_KEY}")
    private String DIFFBOT_API_KEY;

    public Optional<Product> scrapePage(String url) {
        if (url == null || url.isBlank()) {
            log.error("Invalid URL provided: {}", url);
            return Optional.empty();
        }

        try {
            String encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
            String apiUrl = "https://api.diffbot.com/v3";

            WebClient client = WebClient.create(apiUrl);

            URI uri = URI.create(String.format("https://api.diffbot.com/v3/product?token=%s&url=%s",
                    DIFFBOT_API_KEY, encodedUrl));
            log.info("Full uri: {}", uri);

            Mono<String> response = client.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class);

            log.info("Fetched product data: {}", response);

            JSONObject responseJson = new JSONObject(response.block());
            log.info("Response json: {}", responseJson);

            Product product = mapToProduct(responseJson, encodedUrl);

            log.info("Product successfully scraped: {}", product);
            return Optional.of(product);

        } catch (HttpClientErrorException e) {
            log.error("API request failed with status: {}, body {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (RestClientException e) {
            log.error("RestClientException occurred calling Diffbot API ", e);
        } catch (Exception e) {
            log.error("Unexpected error during scraping", e);
        }
        return Optional.empty();
    }

    private Product mapToProduct(JSONObject jsonObject, String url) {
        try {
            JSONObject objects = jsonObject.getJSONArray("objects")
                    .getJSONObject(0);
            log.info("Objects json: {}", objects);


            String imageUrl = objects.getJSONArray("images")
                    .getJSONObject(0)
                    .optString("url");
            log.info("Scraped image url: {}", imageUrl);

            String title = objects.getString("title");
            log.info("Scraped title: {}", title);


            double offerPrice = objects.getJSONObject("offerPriceDetails")
                    .optDouble("amount", 0.0);
            log.info("Scraped offerPrice: {}", offerPrice);

            double regularPrice = 0.0;
            try {
                JSONObject regularPriceDetails = objects.getJSONObject("regularPriceDetails");
                regularPrice = regularPriceDetails.optDouble("amount", 0.0);
                log.info("Scraped regularPrice: {}", regularPrice);

            } catch (Exception e) {
                log.warn("Regular price details not found in JSON for URL: {}", url);
            }


            if (regularPrice == 0.0 && offerPrice > 0) {
                regularPrice = offerPrice;
            }

            return Product.builder()
                    .url(url)
                    .name(title)
                    .image(imageUrl)
                    .price(regularPrice)
                    .priceOnSale(offerPrice)
                    .build();

        } catch (Exception e) {
            log.error("Error mapping JSON to Product, for URL: {}, Exception: {}", url, e.getMessage(), e);
        }


        return Product.builder()
                .url(url)
                .name("Product not found, pls try again")
                .image("https://cdn.pixabay.com/photo/2024/01/31/10/02/kitten-8543772_640.jpg")
                .price(0.0)
                .priceOnSale(0.0)
                .build();
    }
}
