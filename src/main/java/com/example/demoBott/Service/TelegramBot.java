package com.example.demoBott.Service;

import com.example.demoBott.Bottoms.Goals;
import com.example.demoBott.Bottoms.Motivation;
import com.example.demoBott.model.GoalRepository;
import com.example.demoBott.model.User;
import com.example.demoBott.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
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

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GoalRepository goalRepository;
    final BotConfig config;

    private final Map<Long, String> userStates = new HashMap<>();

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

            if (userStates.containsKey(chatId)) {
                switch (userStates.get(chatId)) {
                    case "ADDING_GOAL":
                        Goals addGoal = new Goals(this, goalRepository, userRepository);
                        addGoal.addGoal(chatId, messageText);
                        userStates.remove(chatId);
                        break;
                    case "FINISHING_GOAL":
                        Goals finishGoal = new Goals(this, goalRepository, userRepository);
                        finishGoal.finishGoal(chatId, messageText);
                        userStates.remove(chatId);
                        break;
                }
            } else {
                switch (messageText) {
                    case "/start":
                        registerUser(update.getMessage());
                        sendStartKeyboard(update.getMessage().getChatId());
                        sendMenu(chatId);
                        break;
                    case "Цілі":
                        Goals goals = new Goals(this, goalRepository, userRepository);
                        goals.goalBot(chatId);
                        break;
                    case "Додати ціль":
                        Goals promptAddGoal = new Goals(this, goalRepository, userRepository);
                        promptAddGoal.promptForGoalDescription(chatId);
                        userStates.put(chatId, "ADDING_GOAL");
                        break;
                    case "Мої цілі":
                        Goals goalsList = new Goals(this, goalRepository, userRepository);
                        goalsList.myGoals(chatId);
                        break;
                    case "Завершити ціль":
                        Goals promptFinishGoal = new Goals(this, goalRepository, userRepository);
                        promptFinishGoal.finishGoal(chatId, null);
                        userStates.put(chatId, "FINISHING_GOAL");
                        break;
                    case "Мотивація":
                        Motivation motivation = new Motivation(this);
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
    }


    private void registerUser(Message msg) {
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

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
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
            execute(message);
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
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }
}
