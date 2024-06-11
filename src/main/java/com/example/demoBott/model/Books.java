package com.example.demoBott.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "books")
public class Books {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String author;

    @Getter
    @Setter
    private String link;

    @Setter
    @Getter
    @Lob
    private byte[] content;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "chatId")
    private User user;
}