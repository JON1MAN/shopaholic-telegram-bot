package com.ShopoholicBot.app.dao.model;

import com.ShopoholicBot.app.service.ProductService;
import com.ShopoholicBot.app.service.scraper.ScraperService;
import com.ShopoholicBot.app.service.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private ScraperService scraperService;
    @Autowired
    private UserService userService;

    @Value("${TELEGRAM_BOT_USERNAME}")
    private String BOT_USERNAME;

    @Value("${TELEGRAM_BOT_TOKEN}")
    private String BOT_TOKEN;

    private final String startCommand = "/start";

    private InlineKeyboardButton yes = InlineKeyboardButton.builder()
            .text("Yes")
            .callbackData("YES_BUTTON")
            .build();

    private InlineKeyboardButton no = InlineKeyboardButton.builder()
            .text("No")
            .callbackData("NO_BUTTON")
            .build();

    private InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
            .keyboardRow(List.of(yes, no))
            .build();
    @Autowired
    private ProductService productService;

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null) {
            var message = update.getMessage();
            var chatId = message.getChatId();

            String messageText = message.getText();
            if (messageText.startsWith("/start")) {
                User user = User.builder()
                        .id(chatId)
                        .username(message.getFrom().getUserName())
                        .build();
                userService.create(user);
                log.info("User: {} with id: {}, was created", user.getUsername(), user.getId());

                sendStartResponse(chatId, message.getFrom().getUserName());
            } else if (messageText.startsWith("https")) {
                log.info("User sent a link: {}", messageText);
                Optional<Product> product = scraperService.scrapePage(messageText);

                if (product.isPresent()) {
                    productService.create(product.get());
                    sendInfoOfProductMessage(chatId, product.get());
                }

            }
        }

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();
            Message callBackMessage = callbackQuery.getMessage();
            Long chatId = callBackMessage.getChatId();

            if (callbackData.startsWith("YES_BUTTON:")) {
                Long productId = Long.parseLong(callbackData.split(":")[1]);

                userService.addItemToWishList(productId, chatId);
                log.info("User: {}, with id: {}, added product with id: {}, to wishlist",
                        callBackMessage.getFrom().getUserName(),
                        chatId,
                        productId);

                sendCallBackResponse(callbackQuery.getMessage().getChatId(), "YES BUTTON");
            }
        }
    }

    public void sendInfoOfProductMessage(Long who, Product product) {
        String imageUrl = product.getImage().replace("https", "http");
        String caption = String.format(
                "%s\nPrice: %.2f PLN\nPrice on Sale: %.2f PLN\nIs on Sale: %b",
                product.getName(),
                product.getPrice(),
                product.getPriceOnSale(),
                product.isOnSale()
        );
        SendPhoto sp = SendPhoto.builder()
                .chatId(who.toString())
                .photo(new InputFile(imageUrl))
                .caption(caption)
                .build();

        yes.setCallbackData("YES_BUTTON:" + product.getId());

        try {
            execute(sp);
            sendKeyboard(who, "Do you want to track this Product?", keyboard);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendStartResponse(Long chatId, String userName) {
        String responseText = String.format(
                "Hello %s, I can help you with tracking your favourite products, pls send me a link!",
                userName);
        SendMessage sendMessage = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(responseText)
                .build();

        try {
            execute(sendMessage);
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

    public void sendCallBackResponse(Long chatId, String text) {
        SendMessage sendMessage = SendMessage.builder()
                .text("You choose " + text)
                .chatId(String.valueOf(chatId))
                .build();

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
