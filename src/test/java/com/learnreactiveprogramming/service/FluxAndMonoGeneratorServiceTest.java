package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
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

    @Test
    void exception_onErrorReturn() {
        Flux<String> onErrorReturnFlux = service.exception_onErrorReturn();

        StepVerifier.create(onErrorReturnFlux)
                .expectNext("Alex", "Chloe", "Ben", "Misha")
                .verifyComplete();
    }

    @Test
    void exception_onErrorResume() {
        IllegalStateException illegalStateException = new IllegalStateException("Illegal state exception");

        Flux<String> onErrorResumeFlux = service.exception_onErrorResume(illegalStateException);

        StepVerifier.create(onErrorResumeFlux)
                .expectNext("Alex", "Chloe", "Ben", "Alena")
                .verifyComplete();
    }

    @Test
    void exception_onErrorResume2() {
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Illegal argument exception");

        Flux<String> onErrorResumeFlux = service.exception_onErrorResume(illegalArgumentException);

        StepVerifier.create(onErrorResumeFlux)
                .expectNext("Alex", "Chloe", "Ben")
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void exception_onErrorContinue() {
        Flux<String> onErrorContinueFlux = service.exception_onErrorContinue();

        StepVerifier.create(onErrorContinueFlux)
                .expectNext("Alex", "Ben", "John")
                .verifyComplete();
    }

    @Test
    void exception_onErrorMap() {
        Flux<String> onErrorMapFlux = service.exception_onErrorMap();

        StepVerifier.create(onErrorMapFlux)
                .expectNext("Alex")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void exception_doOnError() {
        Flux<String> doOnErrorFlux = service.exception_doOnError();

        StepVerifier.create(doOnErrorFlux)
                .expectNext("Alex")
                .expectError()
                .verify();
    }

    @Test
    void exception_monoOnErrorReturn() {
        Mono<Object> onErrorReturnMono = service.exception_monoOnErrorReturn();

        StepVerifier.create(onErrorReturnMono)
                .expectNext("Recover value")
                .verifyComplete();
    }


    @Test
    void returnEmptyMono_whenWrongValuePassed() {
        Mono<String> stringMono = service.task("abc");

        StepVerifier.create(stringMono)
                .verifyComplete();
    }

    @Test
    void returnNotEmptyMono_whenCorrectValuePassed() {
        Mono<String> stringMono = service.task("reactor");

        StepVerifier.create(stringMono)
                .expectNext("reactor")
                .verifyComplete();
    }

    @Test
    void namesFlux_concatMapAsync_virtualTimer() {
        VirtualTimeScheduler virtualTimeScheduler = VirtualTimeScheduler.getOrSet();

        Flux<String> chars = service.namesFlux_concatMapAsync();

        StepVerifier.withVirtualTime(() -> chars)
                .thenAwait(Duration.ofSeconds(5))
                .expectNext("A", "l", "e", "x", "C", "h", "l", "o", "e", "B", "e", "n")
                .verifyComplete();
    }

    @Test
    void explore_generate() {
        Flux<Integer> integerFlux = service.explore_generate();

        StepVerifier.create(integerFlux)
                .expectNextCount(9)
                .verifyComplete();
    }


    @Test
    void explore_create() {
        Flux<String> stringFlux = service.explore_create();

        StepVerifier.create(stringFlux)
                .expectNext("alex", "chloe", "ben")
                .verifyComplete();
    }
}
