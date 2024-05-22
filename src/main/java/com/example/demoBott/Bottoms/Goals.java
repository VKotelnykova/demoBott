package com.example.demoBott.Bottoms;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import java.util.List;
import java.util.ArrayList;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Goals {

    public void handleGoalsMenu(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/Додати ціль":

                    break;
                case "/Мої цілі":
                    Goals goals = new Goals();
                    goals.goalBot(chatId, this);
                    break;
                case "/Завершити ціль":

                    break;
                case "/Повернутись назад":

                    break;

                default:
                    sendMessage(chatId, "Sorry, not working");

            }
        }
    }
    public void goalBot(long chatId, TelegramLongPollingBot bot) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Додати ціль");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Мої цілі");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Завершити ціль");

        KeyboardRow row4 = new KeyboardRow();
        row4.add("Повернутись назад");

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);

        keyboardMarkup.setKeyboard(keyboard);
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("");
        message.setReplyMarkup(keyboardMarkup);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Error occurred:" + e.getMessage());
        }
    }
}
