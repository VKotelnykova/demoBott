package com.example.demoBott.handler;

import com.example.demoBott.Bottoms.Goals;
import com.example.demoBott.Bottoms.Motivation;
import com.example.demoBott.Service.TelegramBot;
import com.example.demoBott.model.GoalRepository;
import com.example.demoBott.model.User;
import com.example.demoBott.model.UserRepository;
import jakarta.persistence.Entity;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



@Slf4j

public class CommandHandler {
    private final TelegramBot telegramBot;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final Map<Long, String> userStates;

    public CommandHandler(TelegramBot telegramBot, GoalRepository goalRepository, UserRepository userRepository, Map<Long, String> userStates) {
        this.telegramBot = telegramBot;
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.userStates = userStates;
    }

    public void handleCommand(long chatId, String messageText) {
        if (userStates.containsKey(chatId)) {
            switch (userStates.get(chatId)) {
                case "ADDING_GOAL":
                    Goals addGoal = new Goals(telegramBot, goalRepository, userRepository);
                    addGoal.addGoal(chatId, messageText);
                    userStates.remove(chatId);
                    break;
                case "FINISHING_GOAL":
                    Goals finishGoal = new Goals(telegramBot, goalRepository, userRepository);
                    finishGoal.finishGoal(chatId, messageText);
                    userStates.remove(chatId);
                    break;
            }
        } else {
            switch (messageText) {
                case "/start":
                    Message msg = new Message();
                    registerUser(msg);
                    sendStartKeyboard(chatId);
                    sendMenu(chatId);
                    break;
                case "Цілі":
                    Goals goals = new Goals(telegramBot, goalRepository, userRepository);
                    goals.goalBot(chatId);
                    break;
                case "Додати ціль":
                    Goals promptAddGoal = new Goals(telegramBot, goalRepository, userRepository);
                    promptAddGoal.promptForGoalDescription(chatId);
                    userStates.put(chatId, "ADDING_GOAL");
                    break;
                case "Мої цілі":
                    Goals goalsList = new Goals(telegramBot, goalRepository, userRepository);
                    goalsList.myGoals(chatId);
                    break;
                case "Завершити ціль":
                    Goals promptFinishGoal = new Goals(telegramBot, goalRepository, userRepository);
                    promptFinishGoal.finishGoal(chatId, null);
                    userStates.put(chatId, "FINISHING_GOAL");
                    break;
                case "Видалити ціль":
                    Goals deleteGoal = new Goals(telegramBot, goalRepository, userRepository);
                    deleteGoal.deleteGoal(chatId, null);
                    break;
                case "Мотивація":
                    Motivation motivation = new Motivation(telegramBot);
                    motivation.showMotivationMenu(chatId);
                    break;
                case "Повернутись назад":
                    sendMenu(chatId);
                    break;
                default:
                    sendMessage(chatId, "Команда не розпізнана. Будь ласка, виберіть команду з меню.");
                    break;
            }
        }
    }

    public void registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("User saved: " + user);
        }
    }

    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    public void sendStartKeyboard(long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        KeyboardRow row = new KeyboardRow();
        row.add("/start");

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Welcome to the bot! Press the button below to start.");
        message.setReplyMarkup(keyboardMarkup);

        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    public void sendMenu(long chatId) {
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
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }
}
