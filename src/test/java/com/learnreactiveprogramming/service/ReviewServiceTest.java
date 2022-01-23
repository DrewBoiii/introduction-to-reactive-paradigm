package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Review;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReviewServiceTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();

    ReviewService reviewService = new ReviewService(webClient);

    @Test
    void getReviews() {
        Long movieInfoId = 1L;

        Flux<Review> reviewFlux = reviewService.getReviews(movieInfoId);

        StepVerifier.create(reviewFlux)
                .assertNext(review -> {
                    assertEquals(1L, review.getMovieInfoId());
                    assertEquals(1L, review.getReviewId());
                    assertEquals("Nolan is the real superhero", review.getComment());
                    assertEquals(8.2, review.getRating());
                })
                .verifyComplete();
    }
}