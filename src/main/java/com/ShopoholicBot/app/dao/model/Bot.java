package com.ShopoholicBot.app.dao.model;

import com.ShopoholicBot.app.service.scraper.ScraperService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {

    private ScraperService scraperService;
    @Value("${TeleShopaholicBot}")
    private String BOT_USERNAME;
    @Value("${TELEGRAM_BOT_TOKEN}")
    private String TOKEN;

    private InlineKeyboardButton yes = InlineKeyboardButton.builder()
            .text("Yes")
            .callbackData("YES")
            .build();

    private InlineKeyboardButton no = InlineKeyboardButton.builder()
            .text("No")
            .callbackData("NO")
            .build();

    InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(yes, no))
            .build();

    public Bot() {
        this.scraperService = new ScraperService();
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

        sendInfoOfProductMessage(id, product);
    }

    public void sendInfoOfProductMessage(Long who, Product product){
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
            sendKeyboard(who, "Do you want to track this Product?", keyboard);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendKeyboard(Long who, String text, InlineKeyboardMarkup keyboard) {
        SendMessage message = SendMessage.builder()
                .chatId(who.toString())
                .parseMode("HTML").text(text)
                .replyMarkup(keyboard)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
