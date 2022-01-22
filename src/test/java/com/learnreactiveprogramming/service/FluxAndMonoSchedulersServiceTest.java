package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import reactor.test.StepVerifier;

class FluxAndMonoSchedulersServiceTest {

    FluxAndMonoSchedulersService service = new FluxAndMonoSchedulersService();

    @Test
    void explore_publishOn_noParallel() {
        Flux<String> stringFlux = service.explore_publishOn_noParallel();

        StepVerifier.create(stringFlux)
                .expectNextCount(6)
                .verifyComplete();
    }


    @Test
    void explore_publishOn() {
        Flux<String> stringFlux = service.explore_publishOn();

        StepVerifier.create(stringFlux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void explore_subscribeOn() {
        Flux<String> stringFlux = service.explore_subscribeOn();

        StepVerifier.create(stringFlux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void explore_parallel() {
        ParallelFlux<String> stringFlux = service.explore_parallel();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void explore_parallel_flatMap() {
        Flux<String> stringFlux = service.explore_parallel_flatMap();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void explore_parallel_flatMapSequential() {
        Flux<String> stringFlux = service.explore_parallel_flatMapSequential();

        StepVerifier.create(stringFlux)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }

}