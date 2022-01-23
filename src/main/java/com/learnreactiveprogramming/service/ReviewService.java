package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Review;
import lombok.AllArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.util.List;

@AllArgsConstructor
public class ReviewService {

    private WebClient webClient;

    public Flux<Review> getReviews(Long movieInfoId) {
        String uri = UriComponentsBuilder.fromUriString("/v1/reviews")
                .queryParam("movieInfoId", movieInfoId)
                .buildAndExpand()
                .toUriString();
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(Review.class)
                .map(review -> review)
                .log();
    }

    public List<Review> retrieveReviews(long movieInfoId) {
        return List.of(new Review(1L, movieInfoId, "Awesome Movie", 8.9),
                new Review(2L, movieInfoId, "Excellent Movie", 9.0));
    }

    public Flux<Review> retrieveReviewsFlux(long movieInfoId) {
        var reviewsList = List.of(new Review(1L, movieInfoId, "Awesome Movie", 8.9),
                new Review(2L, movieInfoId, "Excellent Movie", 9.0));
        return Flux.fromIterable(reviewsList);
    }

}
