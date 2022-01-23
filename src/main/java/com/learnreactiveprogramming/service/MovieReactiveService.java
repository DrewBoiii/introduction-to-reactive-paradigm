package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.domain.Revenue;
import com.learnreactiveprogramming.exception.MovieException;
import com.learnreactiveprogramming.exception.NetworkException;
import com.learnreactiveprogramming.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@AllArgsConstructor
public class MovieReactiveService {

    private ReviewService reviewService;
    private MovieInfoService movieInfoService;
    private RevenueService revenueService;

    public Flux<Movie> getMoviesUsingRest() {
        return movieInfoService.getAllMovieInfo_restClient()
                .flatMap(this::buildMovie)
                .doOnError(throwable -> log.error("Movie error", throwable))
                .onErrorMap(MovieException::new)
                .retry(5L)
                .log();
    }

    public Flux<Movie> getAllMovies() {
        return movieInfoService.retrieveMoviesFlux()
                .flatMap(this::getMovie)
                .doOnError(throwable -> log.error("Movie error", throwable))
                .onErrorMap(MovieException::new)
                .retry(5L)
                .log();
    }

    public Flux<Movie> getAllMoviesRetryWhen() {
        return movieInfoService.retrieveMoviesFlux()
                .flatMap(this::getMovie)
                .doOnError(throwable -> log.error("Movie error", throwable))
                .onErrorMap(throwable -> {
                    if (throwable instanceof NetworkException) {
                        throw new MovieException(throwable);
                    }
                    throw new ServiceException(throwable);
                })
                .retryWhen(Retry.backoff(5L, Duration.ofMillis(100L))
                        .filter(throwable -> throwable instanceof MovieException) // use this if you want to specify which of the exception we want to propagate
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()))) // if we don't do this, we get Exhausted Exception instead of Movie exception or any specified exceptions
                .log();
    }

    public Flux<Movie> getAllMoviesRepeat() {
        return movieInfoService.retrieveMoviesFlux()
                .flatMap(this::getMovie)
                .doOnError(throwable -> log.error("Movie error", throwable))
                .onErrorMap(throwable -> {
                    if (throwable instanceof NetworkException) {
                        throw new MovieException(throwable);
                    }
                    throw new ServiceException(throwable);
                })
                .retryWhen(Retry.backoff(5L, Duration.ofMillis(100L))
                        .filter(throwable -> throwable instanceof MovieException)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure())))
                .repeat()
                .log();
    }

    public Flux<Movie> getAllMoviesRepeat(long numberOfRepeats) {
        return movieInfoService.retrieveMoviesFlux()
                .flatMap(this::getMovie)
                .doOnError(throwable -> log.error("Movie error", throwable))
                .onErrorMap(throwable -> {
                    if (throwable instanceof NetworkException) {
                        throw new MovieException(throwable);
                    }
                    throw new ServiceException(throwable);
                })
                .retryWhen(Retry.backoff(5L, Duration.ofMillis(100L))
                        .filter(throwable -> throwable instanceof MovieException)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure())))
                .repeat(numberOfRepeats)
                .log();
    }

    public Mono<Movie> getMovieById(Long id) {
        return movieInfoService.retrieveMovieInfoMonoUsingId(id)
                .flatMap(this::getMovie)
                .log();
    }

    public Mono<Movie> getMovieByIdUsingRest(Long id) {
        return movieInfoService.getMovieInfo(id)
                .flatMap(this::buildMovie)
                .log();
    }

    public Mono<Movie> getMovieByIdWithRevenue(Long id) {
        Mono<Revenue> revenueMono = Mono.fromCallable(() -> revenueService.getRevenue(id))
                .subscribeOn(Schedulers.boundedElastic()); // use this when making blocking calls
        return movieInfoService.retrieveMovieInfoMonoUsingId(id)
                .flatMap(this::getMovie)
                .zipWith(revenueMono, ((movie, revenue) -> {
                    movie.setRevenue(revenue);
                    return movie;
                }))
                .log();
    }

    private Mono<Movie> getMovie(MovieInfo movieInfo) {
        return reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                .collectList()
                .map(reviews -> new Movie(movieInfo, reviews))
                .log();
    }

    private Mono<Movie> buildMovie(MovieInfo movieInfo) {
        return reviewService.getReviews(movieInfo.getMovieInfoId())
                .collectList()
                .map(reviews -> new Movie(movieInfo, reviews))
                .log();
    }

}
