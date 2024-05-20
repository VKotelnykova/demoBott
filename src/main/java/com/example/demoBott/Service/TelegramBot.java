package com.example.demoBott.Service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import java.util.List;
import java.util.ArrayList;
import com.example.demoBott.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;

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

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                sendStartKeyboard(update.getMessage().getChatId());

                sendMenu(chatId);
            } else {
                sendMessage(chatId, "Sorry,command not working");
            }
        }
    }

    private void startCommandReceived(long chatId, String name)  {

        String answer = "Hi, " + name + ", nice to meet you!";
        log.info("Replied to user:" + name);

        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try{
            execute(message);
        }
        catch (TelegramApiException e){
            log.error("Error occurred:" + e.getMessage());
        }
    }
    private void sendStartKeyboard(long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        KeyboardRow row = new KeyboardRow();
        row.add("/start"); // Добавляем кнопку "Start"

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Welcome to the bot! Press the button below to start.");
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred:" + e.getMessage());
        }
    }

    private void sendMenu(long chatId){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Цілі");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Мотивація");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Колесо фортуни");


        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        keyboardMarkup.setKeyboard(keyboard);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Here is the menu:");
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred:" + e.getMessage());
        }
    }
}

