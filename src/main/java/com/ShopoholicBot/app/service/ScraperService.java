package com.ShopoholicBot.app.service;

import java.io.IOException;

import com.ShopoholicBot.app.model.Product;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.springframework.stereotype.Service;

@Service
public class ScraperService {
    public Product scrapePage(String URL){
        Document doc;
        Product product = new Product();

        try{
            doc = Jsoup.connect(URL)
                    .userAgent("User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36")
                    .header("Accept-Language", "*")
                    .get();

            String name = doc
                    .select("span.product-detail-card-info__name")
                    .first()
                    .text()
                    .trim();
            String image = doc
                    .select("img.media-image__image.media__wrapper--media")
                    .first()
                    .attr("src");
            String priceString = doc
                    .select("span.money-amount__main")
                    .first()
                    .text();
            String priceOnSaleString;
            boolean isOnSale = true;
            Element saleSpanCheck = doc
                    .select("span.price__amount--old-price-wrapper")
                    .first();
            if(saleSpanCheck == null){
                isOnSale = false;
                priceOnSaleString = "0.0";
            } else {
                isOnSale = true;
                priceOnSaleString = doc
                        .select("span.money-amount__main")
                        .get(1)
                        .text();
            }
            double price = Double.parseDouble(
                    priceString.substring(0, priceString.indexOf(","))
            );
            double priceOnSale = Double.parseDouble(
                    priceOnSaleString.substring(0, priceString.indexOf(","))
            );


            System.out.println("Name          : " + name);
            System.out.println("Image         : " + image);
            System.out.println("Price         : " + price);
            System.out.println("Price on sale : " + priceOnSale);
            System.out.println("IsOnSale      : " + isOnSale);
            System.out.println("#".repeat(30));

            product.setUrl(URL);
            product.setName(name);
            product.setImage(image);
            product.setPrice(price);
            product.setPriceOnSale(priceOnSale);
            product.setOnSale(isOnSale);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return product;
    }
}
