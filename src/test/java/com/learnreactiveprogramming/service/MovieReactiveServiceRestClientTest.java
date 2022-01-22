package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieReactiveServiceRestClientTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();

    MovieInfoService movieInfoService = new MovieInfoService(webClient);
    ReviewService reviewService = new ReviewService(webClient);

    MovieReactiveService movieReactiveService = new MovieReactiveService(reviewService, movieInfoService, null);

    @Test
    void getMoviesUsingRest() {
        Flux<Movie> moviesFlux = movieReactiveService.getMoviesUsingRest();

        StepVerifier.create(moviesFlux)
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    void getMovieById() {
        Long id = 1L;

        Mono<Movie> movieMono = movieReactiveService.getMovieByIdUsingRest(id);

        StepVerifier.create(movieMono)
                .assertNext(movie -> {
                    assertEquals(1L, movie.getMovie().getMovieInfoId());
                    assertEquals("Batman Begins", movie.getMovie().getName());
                    assertEquals(1, movie.getReviewList().size());
                })
                .verifyComplete();
    }
}