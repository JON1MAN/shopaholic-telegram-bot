package com.ShopoholicBot.app;

import com.ShopoholicBot.app.model.Bot;
import com.ShopoholicBot.app.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class ShopoholicBotApplication {
	public static void main(String[] args) throws TelegramApiException {
		SpringApplication.run(ShopoholicBotApplication.class, args);
		String URL_NOT_SALE = "https://www.zara.com/pl/pl/sweter-z-suwakiem-przy-szyi-p03332310.html?v1=364092262&utm_campaign=productShare&utm_medium=mobile_sharing_iOS&utm_source=red_social_movil#utm_referrer=https%3A%2F%2Fwww.zara.com%2Fshare%2Fquarter-zip-sweater-p03332310.html%3Fv1%3D364092262%26utm_campaign%3DproductShare%26utm_medium%3Dmobile_sharing_iOS%26utm_source%3Dred_social_movil";
		String URL_ON_SALE = "https://www.zara.com/pl/pl/walizka-podrozna-z-kieszeniami-p13106420.html?v1=364107164&v2=2436823";


		TelegramBotsApi bot = new TelegramBotsApi(DefaultBotSession.class);
		bot.registerBot(new Bot());

	}

}
