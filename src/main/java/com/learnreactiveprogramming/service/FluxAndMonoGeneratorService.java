package com.learnreactiveprogramming.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;

@Slf4j
public class FluxAndMonoGeneratorService {

    public Flux<String> getNames() {
        return Flux.fromIterable(names());
    }

    public Mono<String> getName() {
        return Mono.just("Robert");
    }

    public Flux<String> getNamesInUpperCase() {
        return Flux.fromIterable(names())
                .map(String::toUpperCase);
    }

    public Flux<String> getNamesFilteredByNameLengthEqualsTo(int nameLength) {
        return Flux.fromIterable(names())
                .filter(name -> name.length() == 3);
    }

    public Flux<String> checkFluxImmutability() {
        Flux<String> stringFlux = Flux.fromIterable(names());
        stringFlux.map(String::toLowerCase);
        return stringFlux;
    }

    public Mono<String> namesMono_map_filter(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength);
    }

    public Flux<String> namesFlux_flatMap() {
        return Flux.fromIterable(names())
                .take(1)
                .flatMap(this::split)
                .log();
    }

    public Flux<String> namesFlux_flatMapAsync() {
        return Flux.fromIterable(names())
                .flatMap(this::splitWithDelay) // when the order is not matter
                .log();
    }

    public Flux<String> namesFlux_concatMapAsync() {
        return Flux.fromIterable(names())
                .concatMap(this::splitWithDelay) // when the order is matter, but it's slower than flatMap
                .log();
    }

    public Mono<List<String>> namesMonoList_flatMap() {
        return Flux.fromIterable(names())
                .take(1)
                .flatMap(this::split)
                .collectList()
                .log();
    }

    public Flux<String> nameMono_flatMapMany() {
        return Mono.just("Alex")
                .flatMapMany(this::split)
                .log();
    }

    public Flux<String> nameMonoList_flatMapMany() {
        return Mono.just(names())
                .flatMapMany(Flux::fromIterable)
                .take(1)
                .flatMap(this::split)
                .log();
    }

    public Flux<String> namesFlux_transform() {
        return Flux.fromIterable(names())
                .transform(names -> names.map(String::toUpperCase))
                .log();
    }

    public Flux<String> namesMono_map_filter_defaultIfEmpty(int stringLength) {
        return Flux.fromIterable(List.of("ABBA", "AAA"))
                .filter(string -> string.length() > stringLength)
                .defaultIfEmpty("default");
    }

    public Flux<String> namesMono_map_filter_switchIfEmpty(int stringLength) {
        return Flux.fromIterable(List.of("ABBA", "BBB"))
                .filter(string -> string.length() > stringLength)
                .switchIfEmpty(Mono.just("default"));
    }

    private Flux<String> split(String name) {
        String[] split = name.split("");
        return Flux.fromArray(split);
    }

    private Flux<String> splitWithDelay(String name) {
        String[] chars = name.split("");
        int delay = new SecureRandom().nextInt(1000);
        return Flux.fromArray(chars)
                .delayElements(Duration.ofMillis(delay));
    }

    private List<String> names() {
        return List.of("Alex", "Chloe", "Ben");
    }

}
