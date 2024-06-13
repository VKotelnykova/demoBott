package com.example.demoBott.handler;

import com.example.demoBott.Bottoms.Goals;
import com.example.demoBott.Bottoms.Motivation;
import com.example.demoBott.Bottoms.Wheel;
import com.example.demoBott.Service.TelegramBot;
import com.example.demoBott.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.*;

public class CommandHandler {
    private static final Logger log = LoggerFactory.getLogger(CommandHandler.class);
    private final TelegramBot telegramBot;
    private final GoalRepository goalRepository;
    private static UserRepository userRepository;
    private final BooksRepository booksRepository;
    private final QuotesRepository quotesRepository;
    private final VideosRepository videosRepository;
    private final Map<Long, String> userStates;

    public CommandHandler(TelegramBot telegramBot, GoalRepository goalRepository, UserRepository userRepository, BooksRepository booksRepository, QuotesRepository quotesRepository, VideosRepository videosRepository, Map<Long, String> userStates) {
        this.telegramBot = telegramBot;
        this.goalRepository = goalRepository;
        CommandHandler.userRepository = userRepository;
        this.booksRepository = booksRepository;
        this.quotesRepository = quotesRepository;
        this.videosRepository = videosRepository;
        this.userStates = userStates != null ? userStates : new HashMap<>();
    }

    public void handleCommand(long chatId, String messageText, Update update) {
        if (userStates.containsKey(chatId)) {
            handleUserStateCommand(chatId, messageText);
        } else {
            handleGeneralCommand(chatId, messageText, update);
        }
    }

    private void handleUserStateCommand(long chatId, String messageText) {
        String userState = userStates.get(chatId);
        switch (userState) {
            case "ADDING_GOAL", "FINISHING_GOAL" -> {
                Goals goalHandler = new Goals(telegramBot, goalRepository, userRepository);
                if (userState.equals("ADDING_GOAL")) {
                    goalHandler.addGoal(chatId, messageText);
                } else {
                    goalHandler.finishGoal(chatId, messageText);
                }
                userStates.remove(chatId);
            }
            case "ENTERING_NUMBER_OF_POINTS", "ENTERING_POINTS" ->
                    new Wheel(telegramBot).processInput(chatId, messageText);
            default -> sendMessage(chatId, "Невідомий стан користувача: " + userState);
        }
    }

    public void handleGeneralCommand(long chatId, String messageText, Update update) {
        switch (messageText) {
            case "/start" -> {
                registerUser(update.getMessage());
                sendStartKeyboard(update.getMessage().getChatId());
                sendMenu(update.getMessage().getChatId());
            }
            case "Цілі" -> new Goals(telegramBot, goalRepository, userRepository).goalBot(chatId);
            case "Додати ціль" -> {
                new Goals(telegramBot, goalRepository, userRepository).promptForGoalDescription(chatId);
                userStates.put(chatId, "ADDING_GOAL");
            }
            case "Мої цілі" -> new Goals(telegramBot, goalRepository, userRepository).myGoals(chatId);
            case "Завершити ціль" -> {
                new Goals(telegramBot, goalRepository, userRepository).finishGoal(chatId, null);
                userStates.put(chatId, "FINISHING_GOAL");
            }
            case "Мотивація" -> new Motivation(telegramBot).showMotivationMenu(chatId);
            case "Книги" -> sendRandomBook(chatId);
            case "Повернутись назад" -> sendMenu(chatId);
            case "Побажання на день" -> sendRandomQuote(chatId);
            case "Відео" -> sendRandomVideo(chatId);
            case "Колесо фортуни" -> new Wheel(telegramBot).startWheel(chatId);
            default -> sendMessage(chatId, "Команда не розпізнана. Будь ласка, виберіть команду з меню.");
        }
    }

    public static void registerUser(Message msg) {
        if (msg.getChat() != null) {
            long chatId = msg.getChatId();
            if (userRepository.findById(chatId).isEmpty()) {
                User user = new User();
                user.setChatId(chatId);
                user.setFirstName(msg.getChat().getFirstName());
                user.setLastName(msg.getChat().getLastName());
                user.setUserName(msg.getChat().getUserName());
                user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

                userRepository.save(user);
                log.info("Користувача збережено: " + user);
            }
        } else {
            log.error("Отримано нульовий об'єкт Chat у повідомленні.");
        }
    }


    public void sendRandomVideo(long chatId) {
        Optional<Videos> videoOptional = videosRepository.findRandomVideo();
        if (videoOptional.isPresent()) {
            Videos video = videoOptional.get();
            sendMessage(chatId, "Назва: " + video.getTitle() + "\nПосилання: " + video.getLink());
        } else {
            sendMessage(chatId, "На даний момент немає доступних відео.");
        }
    }

    public void sendRandomBook(long chatId) {
        Optional<Books> bookOptional = booksRepository.findRandomBook();
        if (bookOptional.isPresent()) {
            Books book = bookOptional.get();
            String bookInfo = String.format("Назва: %s\nАвтор: %s\nПосилання: %s", book.getTitle(), book.getAuthor(), book.getLink());
            sendMessage(chatId, bookInfo);
        } else {
            sendMessage(chatId, "На даний момент немає доступних книг.");
        }
    }

    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Сталася помилка: " + e.getMessage());
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
        message.setText("Вітаємо в нашому боті\uD83D\uDC96");
        message.setReplyMarkup(keyboardMarkup);

        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Сталася помилка: " + e.getMessage());
        }
    }

    public void sendRandomQuote(long chatId) {
        Optional<Quote> quoteOptional = quotesRepository.findRandomQuote();
        if (quoteOptional.isPresent()) {
            Quote quote = quoteOptional.get();
            sendMessage(chatId, quote.getText());
        } else {
            sendMessage(chatId, "На даний момент немає доступних цитат.");
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
        message.setText("Ось меню:");
        message.setReplyMarkup(keyboardMarkup);

        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Сталася помилка: " + e.getMessage());
        }
    }
}
