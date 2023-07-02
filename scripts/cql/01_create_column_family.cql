
CREATE COLUMNFAMILY IF NOT EXISTS reviews.review (
                             productId text PRIMARY KEY,
                             averageReviewScore double,
                             numberOfReviews int
);

