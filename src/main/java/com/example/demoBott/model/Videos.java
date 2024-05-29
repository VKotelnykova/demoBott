package com.example.demoBott.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
    @Table(name = "videos")
    public class Videos {

    @Getter
    @Setter
    @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Setter
        @Getter
        private String title;
        @Setter
        @Getter
        private String link;

        @ManyToOne
        @JoinColumn(name = "user_id", referencedColumnName = "chatId")
        private User user;
    }
