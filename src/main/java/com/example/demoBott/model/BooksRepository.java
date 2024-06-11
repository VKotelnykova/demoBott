package com.example.demoBott.model;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BooksRepository extends CrudRepository<Books, Long> {
    @Query(value = "SELECT * FROM books ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Books> findRandomBook();
}