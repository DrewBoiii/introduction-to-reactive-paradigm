package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MovieReactiveServiceTest {

    //    @InjectMocks
//    MovieReactiveService movieReactiveService;
//
//    @Mock
//    ReviewService reviewService;
//    @Mock
//    MovieInfoService movieInfoService;
    ReviewService reviewService = new ReviewService();
    MovieInfoService movieInfoService = new MovieInfoService();
    MovieReactiveService movieReactiveService = new MovieReactiveService(reviewService, movieInfoService);

    @Test
    void getAllMovies() {
        Flux<Movie> allMovies = movieReactiveService.getAllMovies();

        StepVerifier.create(allMovies)
                .assertNext(movie -> {
                    assertEquals(100L, movie.getMovie().getMovieInfoId());
                    assertEquals("Batman Begins", movie.getMovie().getName());
                    assertEquals(2, movie.getReviewList().size());
                })
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getMovieById() {
        long id = 100L;

        Mono<Movie> movieById = movieReactiveService.getMovieById(id);

        StepVerifier.create(movieById)
                .assertNext(movie -> assertEquals(100L, movie.getMovie().getMovieInfoId()))
                .verifyComplete();
    }
}