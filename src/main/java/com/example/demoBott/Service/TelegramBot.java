package com.example.demoBott.Service;

import com.example.demoBott.handler.CommandHandler;
import com.example.demoBott.model.GoalRepository;
import com.example.demoBott.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;
import com.example.demoBott.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GoalRepository goalRepository;

    public final Map<Long, String> userStates = new HashMap<>();
    private Map<Long, String> userStatesData;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    public void setUserStateData(long chatId, String data) {
        userStatesData.put(chatId, data);
    }

    public void setUserState(long chatId, String state) {
        userStates.put(chatId, state);
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            CommandHandler commandHandler = new CommandHandler(this, goalRepository, userRepository, userStates);
            commandHandler.handleCommand(chatId, messageText);
        }
    }
}