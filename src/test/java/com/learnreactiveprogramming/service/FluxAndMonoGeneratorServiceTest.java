package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

public class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService service = new FluxAndMonoGeneratorService();

    @Test
    void testGetNames() {
        Flux<String> names = service.getNames();

        StepVerifier.create(names)
                .expectNext("Alex")
                .expectNextMatches(name -> name.startsWith("Ch"))
                .expectNext("Ben")
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void testName() {
        Mono<String> name = service.getName();

        StepVerifier.create(name)
                .expectNext("Robert")
                .verifyComplete();
    }

    @Test
    void testGetNamesInUpperCase() {
        Flux<String> names = service.getNamesInUpperCase();

        StepVerifier.create(names)
                .expectNext("ALEX", "CHLOE", "BEN")
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void getNamesFilteredByNameLengthEqualsToThree() {
        Flux<String> names = service.getNamesFilteredByNameLengthEqualsTo(3);

        StepVerifier.create(names)
                .expectNext("Ben")
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void checkFluxImmutability() {
        Flux<String> stringFlux = service.checkFluxImmutability();

        //no lower case names
        StepVerifier.create(stringFlux)
                .expectNext("Alex")
                .expectNextCount(2L)
                .verifyComplete();
    }

    @Test
    void namesMono_map_filter_lengthIsGreaterThenThree() {
        Mono<String> stringMono = service.namesMono_map_filter(3);

        StepVerifier.create(stringMono)
                .expectNext("ALEX")
                .verifyComplete();
    }


    @Test
    void namesFlux_flatMap() {
        Flux<String> chars = service.namesFlux_flatMap();

        StepVerifier.create(chars)
                .expectNext("A", "l", "e", "x")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatMapAsync() {
        Flux<String> chars = service.namesFlux_flatMapAsync();

        StepVerifier.create(chars)
//                .expectNext("A", "l", "e", "x", "B", "e", "n", "C", "h", "l", "o", "e")
                .expectNextCount(12)
                .verifyComplete();
    }


    @Test
    void namesFlux_concatMapAsync() {
        Flux<String> chars = service.namesFlux_concatMapAsync();

        StepVerifier.create(chars)
                .expectNext("A", "l", "e", "x", "C", "h", "l", "o", "e", "B", "e", "n")
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void namesMonoList_flatMap() {
        Mono<List<String>> chars = service.namesMonoList_flatMap();

        StepVerifier.create(chars)
                .expectNext(List.of("A", "l", "e", "x"))
                .verifyComplete();
    }

    @Test
    void nameMono_flatMapMany() {
        Flux<String> chars = service.nameMono_flatMapMany();

        StepVerifier.create(chars)
                .expectNext("A", "l", "e", "x")
                .verifyComplete();
    }

    @Test
    void nameMonoList_flatMapMany() {
        Flux<String> chars = service.nameMonoList_flatMapMany();

        StepVerifier.create(chars)
                .expectNext("A", "l", "e", "x")
                .verifyComplete();
    }

    @Test
    void namesMono_map_filter_defaultIfEmpty() {
        Flux<String> stringFlux = service.namesMono_map_filter_defaultIfEmpty(4);

        StepVerifier.create(stringFlux)
                .expectNext("default")
                .verifyComplete();
    }


    @Test
    void namesMono_map_filter_switchIfEmpty() {
        Flux<String> stringFlux = service.namesMono_map_filter_switchIfEmpty(4);

        StepVerifier.create(stringFlux)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void explore_concatWith() {
        Flux<String> stringFlux = service.explore_concatWith();

        StepVerifier.create(stringFlux)
                .expectNext("A", "B", "C", "D")
                .verifyComplete();
    }

    @Test
    void explore_concatWith_mono() {
        Flux<String> stringFlux = service.explore_concatWith_mono();

        StepVerifier.create(stringFlux)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void explore_mergeWith() {
        Flux<String> stringFlux = service.explore_mergeWith();

        StepVerifier.create(stringFlux)
                .expectNext("A", "C", "B", "D", "E")
                .verifyComplete();
    }

    @Test
    void explore_mergeWith_mono() {
        Flux<String> stringFlux = service.explore_mergeWith_mono();

        StepVerifier.create(stringFlux)
                .expectNext("A", "B")
                .verifyComplete();
    }


    @Test
    void explore_mergeSequential() {
        Flux<String> stringFlux = service.explore_mergeSequential();

        StepVerifier.create(stringFlux)
                .expectNext("A", "B", "C", "D", "E")
                .verifyComplete();
    }


    @Test
    void explore_zip() {
        Flux<String> stringFlux = service.explore_zip();

        StepVerifier.create(stringFlux)
                .expectNext("AC", "BD")
                .verifyComplete();
    }

    @Test
    void exception_flux() {
        Flux<String> exceptionFlux = service.exception_flux();

        StepVerifier.create(exceptionFlux)
                .expectNextCount(3)
                .expectError()
                .verify();
    }
}
