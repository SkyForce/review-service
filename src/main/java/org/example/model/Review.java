package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;


@Data
@Builder
@Table
@AllArgsConstructor
public class Review {
    @PrimaryKey
    private String productId;

    private Double averageReviewScore;

    private Integer numberOfReviews;
}
