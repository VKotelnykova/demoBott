package com.example.demoBott.Bottoms;


import com.example.demoBott.Service.TelegramBot;
import com.example.demoBott.model.Goal;
import com.example.demoBott.model.GoalRepository;
import com.example.demoBott.model.UserRepository;
import com.example.demoBott.model.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

public class Goals {

    private final TelegramBot telegramBot;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public Goals(TelegramBot telegramBot, GoalRepository goalRepository, UserRepository userRepository) {
        this.telegramBot = telegramBot;
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    public void promptForGoalDescription(long chatId) {
        sendMessage(chatId, "Введіть опис цілі:");
    }

    public void addGoal(long chatId, String goalDescription) {
        User user = userRepository.findById(chatId).orElse(null);
        if (user != null) {
            Goal goal = new Goal();
            goal.setUser(user);
            goal.setDescription(goalDescription);
            goal.setCompleted(false);
            goal.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            goalRepository.save(goal);
            sendMessage(chatId, "Ціль додано: " + goalDescription);
        } else {
            sendMessage(chatId, "Користувач не знайдений");
        }
    }

    public void myGoals(long chatId) {
        User user = userRepository.findById(chatId).orElse(null);
        if (user != null) {
            List<Goal> goals = goalRepository.findByUserChatIdAndCompletedFalse(chatId);
            StringBuilder response = new StringBuilder("Ваші цілі:\n");
            for (Goal goal : goals) {
                response.append(goal.getId()).append(": ").append(goal.getDescription()).append("\n");
            }
            sendMessage(chatId, response.toString());
        } else {
            sendMessage(chatId, "Користувач не знайдений");
        }
    }

    public void promptForGoalId(long chatId) {
        sendMessage(chatId, "Введіть ID цілі для завершення:");
    }

    public void finishGoal(long chatId, String goalId) {
        try {
            long id = Long.parseLong(goalId);
            Goal goal = goalRepository.findById(id).orElse(null);
            if (goal != null && goal.getUser().getChatId() == chatId) {
                goal.setCompleted(true);
                goalRepository.save(goal);
                sendMessage(chatId, "Ціль завершено: " + goal.getDescription());
            } else {
                sendMessage(chatId, "Ціль не знайдено або ви не маєте права завершити цю ціль");
            }
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Невірний ID цілі");
        }
    }

    public void goalBot(long chatId) {
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
        message.setText("Here is the Goals menu:");
        message.setReplyMarkup(keyboardMarkup);

        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
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
