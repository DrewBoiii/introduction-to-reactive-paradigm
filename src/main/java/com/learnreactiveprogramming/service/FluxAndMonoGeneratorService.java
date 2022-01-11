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

    public Flux<String> explore_concat() {
        Flux<String> ab = Flux.just("A", "B");
        Flux<String> cd = Flux.just("C", "D");
        return Flux.concat(ab, cd); // publishers are subscribed sequentially
    }

    public Flux<String> explore_concatWith_mono() {
        Mono<String> aMono = Mono.just("A");
        Mono<String> bMono = Mono.just("B");
        return aMono.concatWith(bMono);
    }

    public Flux<String> explore_concatWith() {
        Flux<String> ab = Flux.just("A", "B");
        Flux<String> cd = Flux.just("C", "D");
        return ab.concatWith(cd);
    }

    public Flux<String> explore_mergeWith() {
        Flux<String> ab = Flux.just("A", "B").delayElements(Duration.ofMillis(100));
        Flux<String> cde = Flux.just("C", "D", "E").delayElements(Duration.ofMillis(200));
        return ab.mergeWith(cde).log(); // publishers are subscribed simultaneously
    }

    public Flux<String> explore_mergeWith_mono() {
        Mono<String> aMono = Mono.just("A");
        Mono<String> bMono = Mono.just("B");
        return aMono.mergeWith(bMono).log(); // publishers are subscribed simultaneously
    }

    public Flux<String> explore_mergeSequential() {
        Flux<String> ab = Flux.just("A", "B").delayElements(Duration.ofMillis(100));
        Flux<String> cde = Flux.just("C", "D", "E").delayElements(Duration.ofMillis(200));
        return Flux.mergeSequential(ab, cde).log(); // when the ordering matters
    }

    public Flux<String> explore_zip() {
        Flux<String> ab = Flux.just("A", "B");
        Flux<String> cde = Flux.just("C", "D", "E");
        return Flux.zip(ab, cde, String::concat).log();
    }

    public Flux<String> exception_flux() {
        return Flux.fromIterable(names())
                .concatWith(Flux.error(new RuntimeException("Error occurred")))
                .concatWith(Flux.just("Misha"))
                .log();
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
