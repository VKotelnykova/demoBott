package com.example.demoBott.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

public class Books {
    @Entity
    @Table(name = "books")
    public class Book {

        @Setter
        @Getter
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Getter
        @Setter
        private String title;
        @Setter
        @Getter
        private String author;
        @Setter
        @Getter
        @Lob
        private byte[] content;

        @ManyToOne
        @JoinColumn(name = "user_id", referencedColumnName = "chatId")
        private User user;
    }
}
