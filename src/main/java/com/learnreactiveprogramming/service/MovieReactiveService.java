package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.exception.MovieException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
public class MovieReactiveService {

    private ReviewService reviewService;
    private MovieInfoService movieInfoService;

    public Flux<Movie> getAllMovies() {
        return movieInfoService.retrieveMoviesFlux()
                .flatMap(this::getMovie)
                .doOnError(throwable -> log.error("Movie error", throwable))
                .onErrorMap(MovieException::new)
                .retry(5L)
                .log();
    }

    public Mono<Movie> getMovieById(Long id) {
        return movieInfoService.retrieveMovieInfoMonoUsingId(id)
                .flatMap(this::getMovie)
                .log();
    }

    private Mono<Movie> getMovie(MovieInfo movieInfo) {
        return reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList()
                .map(reviews -> new Movie(movieInfo, reviews))
                .log();
    }

}
