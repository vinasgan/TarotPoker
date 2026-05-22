package org.example.tarotpokerapplication;

import org.example.tarotpokerapplication.bot.TarotPokerBot;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class TarotPokerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TarotPokerApplication.class, args);
    }

    @Bean
    public ApplicationRunner botRegistrar(TarotPokerBot bot) {
        return args -> {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(bot);
            bot.registerCommands();
        };
    }
}
