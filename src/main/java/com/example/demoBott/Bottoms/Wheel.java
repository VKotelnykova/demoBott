package com.example.demoBott.Bottoms;

import com.example.demoBott.Service.TelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Wheel {
    private final TelegramBot telegramBot;

    public Wheel(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void startWheel(long chatId) {
        sendMessage(chatId, "Скільки пунктів ви хочете ввести для вибору?");
        telegramBot.setUserState(chatId, "ENTERING_NUMBER_OF_POINTS"); // Встановлюємо стан для введення кількості пунктів
    }

    public void processInput(long chatId, String input) {
        String currentState = telegramBot.userStates.get(chatId);

        if ("ENTERING_NUMBER_OF_POINTS".equals(currentState)) {
            try {
                int numPoints = Integer.parseInt(input);
                if (numPoints <= 0) {
                    sendMessage(chatId, "Будь ласка, введіть додатне число.");
                    return;
                }

                sendMessage(chatId, "Введіть " + numPoints + " пунктів через кому:");
                telegramBot.setUserState(chatId, "ENTERING_POINTS"); // Встановлюємо стан для введення пунктів
            } catch (NumberFormatException e) {
                sendMessage(chatId, "Будь ласка, введіть коректне число.");
            }
        } else if ("ENTERING_POINTS".equals(currentState)) {
            List<String> points = Arrays.asList(input.split("\\s*,\\s*"));
            chooseRandomPoint(chatId, points);
            telegramBot.userStates.remove(chatId); // Видаляємо стан користувача після завершення
        } else {
            sendMessage(chatId, "Невідомий стан. Почніть спочатку.");
        }
    }

    public void chooseRandomPoint(long chatId, List<String> points) {
        Random random = new Random();
        int randomIndex = random.nextInt(points.size());
        String randomPoint = points.get(randomIndex);
        sendMessage(chatId, "Випадково обрано: " + randomPoint);
    }

    private void sendMessage(long chatId, String textToSend) {
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