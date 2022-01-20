package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
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
}