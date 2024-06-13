package com.example.demoBott.Bottoms;

import com.example.demoBott.Service.TelegramBot;
import com.example.demoBott.model.Goal;
import com.example.demoBott.model.GoalRepository;
import com.example.demoBott.model.User;
import com.example.demoBott.model.UserRepository;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
            List<Goal> goals = goalRepository.findByUserChatId(chatId);
            if (goals.isEmpty()) {
                sendMessage(chatId, "У вас немає жодної цілі");
            } else {
                StringBuilder message = new StringBuilder("Ваші цілі:\n\n");
                for (Goal goal : goals) {
                    String statusEmoji = goal.isCompleted() ? "✅" /* Галочка */ : "❌" /* Хрестик */;
                    message.append("- ")
                            .append(goal.getDescription())
                            .append(statusEmoji)
                            .append("\n\n");
                }
                sendMessage(chatId, message.toString());
            }
        } else {
            sendMessage(chatId, "Користувач не знайдений");
        }
    }

    public void finishGoal(long chatId, String goalIdStr) {
        User user = userRepository.findById(chatId).orElse(null);
        if (user != null) {
            if (goalIdStr == null) {
                List<Goal> goals = goalRepository.findByUserChatIdAndCompletedFalse(chatId);
                if (goals.isEmpty()) {
                    sendMessage(chatId, "У вас немає жодної цілі для завершення");
                } else {
                    StringBuilder message = new StringBuilder("Введіть номер цілі для завершення:\n");
                    for (int i = 0; i < goals.size(); i++) {
                        message.append(i + 1).append(". ")
                                .append(goals.get(i).getDescription())
                                .append("\n");
                    }
                    sendMessage(chatId, message.toString());
                }
            } else {
                try {
                    int goalIndex = Integer.parseInt(goalIdStr) - 1;
                    List<Goal> goals = goalRepository.findByUserChatIdAndCompletedFalse(chatId);
                    if (goalIndex >= 0 && goalIndex < goals.size()) {
                        Goal goal = goals.get(goalIndex);
                        if (!goal.isCompleted()) {
                            goal.setCompleted(true);
                            goalRepository.save(goal);
                            sendMessage(chatId, "Ціль завершена: " + goal.getDescription());
                        } else {
                            sendMessage(chatId, "Ціль вже була завершена раніше.");
                        }
                    } else {
                        sendMessage(chatId, "Неправильний номер цілі. Спробуйте ще раз.");
                    }
                } catch (NumberFormatException e) {
                    sendMessage(chatId, "Неправильний формат номера цілі. Спробуйте ще раз.");
                }
            }
        } else {
            sendMessage(chatId, "Користувач не знайдений");
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
        message.setText("Меню цілей:");
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
