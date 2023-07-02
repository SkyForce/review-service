package org.example.repository;

import org.springframework.data.repository.CrudRepository;

import org.example.model.Review;

public interface ReviewRepository extends CrudRepository<Review, String> {
}
