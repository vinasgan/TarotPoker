package org.example.tarotpokerapplication.bot;

import lombok.extern.slf4j.Slf4j;
import org.example.tarotpokerapplication.host.NgrokService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.List;

@Slf4j
@Component
public class TarotPokerBot extends TelegramLongPollingBot {

    private final NgrokService ngrokService;

    @Value("${telegram.bot.username}")
    private String botUsername;

    public TarotPokerBot(NgrokService ngrokService,
                         @Value("${telegram.bot.token}") String token) {
        super(token);
        this.ngrokService = ngrokService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    public void registerCommands() {
        try {
            super.execute(SetMyCommands.builder()
                    .command(BotCommand.builder().command("start").description("Welcome to Tarot Poker").build())
                    .command(BotCommand.builder().command("play").description("Open the game").build())
                    .command(BotCommand.builder().command("help").description("Help & rules").build())
                    .scope(new BotCommandScopeDefault())
                    .languageCode(null)
                    .build());
            log.info("Bot commands registered.");
        } catch (Exception e) {
            log.warn("Failed to register bot commands: {}", e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String text = update.getMessage().getText().trim();
        long chatId = update.getMessage().getChatId();

        if (text.startsWith("/start") || text.startsWith("/play")) {
            handlePlay(chatId);
        } else if (text.startsWith("/help")) {
            send(chatId, helpText());
        } else {
            send(chatId, "Use /play to open the game, or /help for rules.");
        }
    }

    private void handlePlay(long chatId) {
        execute(SendMessage.builder()
                .chatId(chatId)
                .text("🃏 *Tarot Poker*\n\nPoker with a mystical twist — play Major Arcana event cards to turn the tide!\n\nTap *Play* to open the game.")
                .parseMode("Markdown")
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(
                                InlineKeyboardButton.builder()
                                        .text("🎮 Play Tarot Poker")
                                        .webApp(new WebAppInfo(ngrokService.getPublicUrl()))
                                        .build()
                        ))
                        .build())
                .build());
    }

    private void execute(SendMessage msg) {
        try {
            super.execute(msg);
        } catch (Exception e) {
            log.error("Failed to send message to chat {}", msg.getChatId(), e);
        }
    }

    private void send(long chatId, String text) {
        execute(SendMessage.builder().chatId(chatId).text(text).build());
    }

    private String displayName(User u) {
        if (u.getFirstName() != null && !u.getFirstName().isBlank()) return u.getFirstName();
        if (u.getUserName() != null && !u.getUserName().isBlank()) return u.getUserName();
        return "Player#" + u.getId();
    }

    private String helpText() {
        return """
                🃏 Tarot Poker Bot

                /play — Open the game
                /help — This help message

                Rules: First to 3 round wins wins the match.
                Each round has 3 Event Windows.
                You may play at most 2 Major Arcana cards per round.
                """;
    }
}
