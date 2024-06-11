package com.example.demoBott.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VideosRepository extends CrudRepository<Videos, Long> {
    @Query(value = "SELECT * FROM videos ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Videos> findRandomVideo();
}