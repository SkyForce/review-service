package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.Review;
import org.example.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{productId}")
    public Review getReview(@PathVariable String productId) {
        return reviewService.getReview(productId);
    }

    @DeleteMapping("/{productId}")
    public void deleteReview(@PathVariable String productId) {
        reviewService.deleteReview(productId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createReview(@RequestBody Review review) {
        reviewService.createReview(review);
    }

    @PutMapping("/{productId}")
    public void updateReview(@PathVariable String productId, @RequestBody Review review) {
        reviewService.updateReview(productId, review);
    }
}
