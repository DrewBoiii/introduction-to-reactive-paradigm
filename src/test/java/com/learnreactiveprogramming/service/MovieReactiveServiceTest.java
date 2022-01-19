package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.exception.MovieException;
import com.learnreactiveprogramming.exception.NetworkException;
import com.learnreactiveprogramming.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieReactiveServiceTest {

    @InjectMocks
    MovieReactiveService movieReactiveService;

    @Mock
    ReviewService reviewService;
    @Mock
    MovieInfoService movieInfoService;

    @Test
    void getAllMovies() {
        when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenCallRealMethod();

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
    void negativeTestGetAllMovies() {
        when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(RuntimeException.class);

        Flux<Movie> allMovies = movieReactiveService.getAllMovies();

        StepVerifier.create(allMovies)
                .expectError(MovieException.class)
                .verify();

        verify(reviewService, times(6)).retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getMovieById() {
        long id = 100L;

        when(movieInfoService.retrieveMovieInfoMonoUsingId(anyLong()))
                .thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenCallRealMethod();

        Mono<Movie> movieById = movieReactiveService.getMovieById(id);

        StepVerifier.create(movieById)
                .assertNext(movie -> assertEquals(100L, movie.getMovie().getMovieInfoId()))
                .verifyComplete();
    }

    @Test
    void getAllMoviesRetryWhen1() {
        when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(RuntimeException.class);

        Flux<Movie> allMovies = movieReactiveService.getAllMoviesRetryWhen();

        StepVerifier.create(allMovies)
                .expectError(ServiceException.class)
                .verify();

        verify(reviewService, times(1)).retrieveReviewsFlux(isA(Long.class)); // retry only if we had movie exception, we specified it, that's why it called once
    }

    @Test
    void getAllMoviesRetryWhen2() {
        when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(NetworkException.class);

        Flux<Movie> allMovies = movieReactiveService.getAllMoviesRetryWhen();

        StepVerifier.create(allMovies)
                .expectError(MovieException.class)
                .verify();

        verify(reviewService, times(6)).retrieveReviewsFlux(isA(Long.class));
    }
}