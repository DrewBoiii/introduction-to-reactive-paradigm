package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.MovieInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MovieInfoServiceTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();

    MovieInfoService movieInfoService = new MovieInfoService(webClient);

    @Test
    void getAllMovieInfo_restClient() {

        Flux<MovieInfo> movieInfoFlux = movieInfoService.getAllMovieInfo_restClient();

        StepVerifier.create(movieInfoFlux)
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    void getMovieInfo() {
        long id = 1L;

        Mono<MovieInfo> movieInfoMono = movieInfoService.getMovieInfo(id);

        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> {
                    assertEquals(1L, movieInfo.getMovieInfoId());
                    assertEquals("Batman Begins", movieInfo.getName());
                    assertEquals(2005, movieInfo.getYear());
                    assertEquals(2, movieInfo.getCast().size());
                })
                .verifyComplete();
    }
}