package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.MovieInfo;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class MovieReactiveService {

    private ReviewService reviewService;
    private MovieInfoService movieInfoService;

    public Flux<Movie> getAllMovies() {
        return movieInfoService.retrieveMoviesFlux()
                .flatMap(this::getMovie)
                .log();
    }

    public Mono<Movie> getMovieById(Long id) {
        return movieInfoService.retrieveMovieInfoMonoUsingId(id)
                .flatMap(this::getMovie)
                .log();
//        Mono<MovieInfo> movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(id);
//        Mono<List<Review>> reviewFlux = reviewService.retrieveReviewsFlux(id).collectList();
//        return movieInfoMono.zipWith(reviewFlux, Movie::new)
//                .log();
    }

    private Mono<Movie> getMovie(MovieInfo movieInfo) {
        return reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList()
                .map(reviews -> new Movie(movieInfo, reviews));
    }

}
