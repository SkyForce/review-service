package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.AlreadyExistsException;
import org.example.model.Review;
import org.example.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final Map<String, Review> reviewsCache;

    public Review getReview(String productId) {
        return Optional.ofNullable(reviewsCache.get(productId)).or(() -> {
            Optional<Review> reviewFromRepo = reviewRepository.findById(productId);
            reviewFromRepo.ifPresent(review -> reviewsCache.put(review.getProductId(), review));
            return reviewFromRepo;
        }).orElseThrow();
    }

    public void deleteReview(String productId) {
        log.info("Deleting review for productId {}", productId);

        reviewsCache.remove(productId);
        reviewRepository.deleteById(productId);

        log.info("Review for productId {} deleted", productId);
    }

    public void createReview(Review review) {
        log.info("Creating review {}", review);

        reviewRepository.findById(review.getProductId())
                .ifPresentOrElse(rev -> {
                        log.info("Review for productId {} already exists", review.getProductId());
                        throw new AlreadyExistsException();
                    }, () -> {
                    reviewRepository.save(review);
                    reviewsCache.put(review.getProductId(), review);

                    log.info("Review for productId {} created", review.getProductId());
                });
    }

    public void updateReview(String productId, Review review) {
        log.info("Updating review for productId {} with content {}", productId, review);

        reviewRepository.findById(productId)
                .ifPresentOrElse(rev -> {
                    Review entity = Review.builder()
                            .productId(productId)
                            .averageReviewScore(review.getAverageReviewScore())
                            .numberOfReviews(review.getNumberOfReviews())
                            .build();
                    reviewRepository.save(entity);
                    reviewsCache.put(entity.getProductId(), entity);

                    log.info("Review for productId {} updated", productId);
                }, () -> {
                    log.info("Review for productId {} doesn't exist", productId);
                    throw new NoSuchElementException();
                });
    }
}
