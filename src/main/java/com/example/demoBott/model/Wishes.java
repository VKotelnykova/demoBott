package com.example.demoBott.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
    @Table(name = "wishes")
    public class Wishes {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "user_id", referencedColumnName = "chatId")

        private User user;
        private String wish;


}

