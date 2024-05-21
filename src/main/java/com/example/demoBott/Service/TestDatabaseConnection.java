package com.example.demoBott.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.demoBott.model.UserRepository;
@Component
public class TestDatabaseConnection implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Підключення до бази даних успішне: " + userRepository.count());
    }
}