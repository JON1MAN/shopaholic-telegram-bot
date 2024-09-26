package com.ShopoholicBot.app.model;

import com.ShopoholicBot.app.service.ScraperService;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Bot extends TelegramLongPollingBot {

    private ScraperService scraperService;
    private Dotenv dotenv;
    private String BOT_USERNAME;
    private String TOKEN;

    public Bot() {
        this.scraperService = new ScraperService();
        this.dotenv = Dotenv.load();
        this.BOT_USERNAME = dotenv.get("TELEGRAM_BOT_USERNAME");
        this.TOKEN = dotenv.get("TELEGRAM_BOT_TOKEN");
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var user = update.getMessage().getFrom();
        var message = update.getMessage();
        var id = user.getId();

        System.out.println("User: " + user.getFirstName());
        String messageText = message.getText();
        System.out.println("Wrote:" + messageText);
        Product product = scraperService.scrapePage(messageText);

        sendMessage(id, product);
    }

    public void sendMessage(Long who, Product product){
        String caption = String.format(
                "%s\nPrice: %.2f PLN\nPrice on Sale: %.2f PLN\nIs on Sale: %b",
                product.getName(),
                product.getPrice(),
                product.getPriceOnSale(),
                product.isOnSale()
        );
        SendPhoto sp = SendPhoto.builder()
                .chatId(who.toString())
                .photo(new InputFile(product.getUrl()))
                .caption(caption)
                .build();

        try{
            execute(sp);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
