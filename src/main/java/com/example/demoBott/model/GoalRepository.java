package com.example.demoBott.model;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface GoalRepository extends CrudRepository<Goal, Long> {
    List<Goal> findByUserChatIdAndCompletedFalse(Long chatId);
    List<Goal> findByUserChatId(Long chatId);
}
