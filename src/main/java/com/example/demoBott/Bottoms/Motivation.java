package com.example.demoBott.Bottoms;

import com.example.demoBott.Service.TelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class Motivation {



    private final TelegramBot telegramBot;

    public Motivation(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void showMotivationMenu(long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Побажання на день");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Книги");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Відео");

        KeyboardRow row4 = new KeyboardRow();
        row4.add("Повернутись назад ");

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);

        keyboardMarkup.setKeyboard(keyboard);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Меню мотивації:");
        message.setReplyMarkup(keyboardMarkup);

        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }

    public void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }
}