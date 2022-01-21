package com.learnreactiveprogramming.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

@Slf4j
public class FluxAndMonoSchedulersService {

    static List<String> namesList1 = List.of("alex", "ben", "chloe");
    static List<String> namesList2 = List.of("adam", "jill", "jack");

    public Flux<String> explore_publishOn_noParallel() {
        Flux<String> namesFlux1 = Flux.fromIterable(namesList1)
                .map(this::upperCaseWithDelay)
                .log();

        Flux<String> namesFlux2 = Flux.fromIterable(namesList2)
                .map(this::upperCaseWithDelay)
                .log();

        return namesFlux1.mergeWith(namesFlux2);
    }

    public Flux<String> explore_publishOn() {
        Flux<String> namesFlux1 = Flux.fromIterable(namesList1)
//                .publishOn(Schedulers.parallel()) // boundedElastic is preferable
                .publishOn(Schedulers.boundedElastic()) // use this when make a blocking call, create new parallel thread
                .map(this::upperCaseWithDelay)
                .log();

        Flux<String> namesFlux2 = Flux.fromIterable(namesList2)
                .doOnNext(name -> log.info("Name is {}", name))
                .publishOn(Schedulers.boundedElastic()) // the important thing is that all the functions bellow will be executed in parallel thread. influences the thread downstream. functions above won't be executed in parallel thread.
                .map(this::upperCaseWithDelay)
                .log();

        return namesFlux1.mergeWith(namesFlux2);
    }

    public Flux<String> explore_subscribeOn() {
        Flux<String> namesFlux1 = Flux.fromIterable(namesList1)
                .map(this::upperCaseWithDelay)
                .subscribeOn(Schedulers.boundedElastic()) // influences the thread upstream, actually the whole pipeline
                .log();

        Flux<String> namesFlux2 = Flux.fromIterable(namesList2)
                .map(this::upperCaseWithDelay)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(name -> log.info("Name is {}", name))
                .log();

        return namesFlux1.mergeWith(namesFlux2);
    }

    public ParallelFlux<String> explore_parallel() {
        System.out.println("Number of processors is " + Runtime.getRuntime().availableProcessors());

        return Flux.fromIterable(namesList1)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(this::upperCaseWithDelay)
                .log();
    }


    public Flux<String> explore_parallel_flatMap() {
        return Flux.fromIterable(namesList1)
                .flatMap(name -> Mono.just(name)
                        .map(this::upperCaseWithDelay)
                        .subscribeOn(Schedulers.parallel()))
                .log();
    }

    public Flux<String> explore_parallel_flatMapSequential() {
        return Flux.fromIterable(namesList1)
                .flatMapSequential(name -> Mono.just(name)
                        .map(this::upperCaseWithDelay)
                        .subscribeOn(Schedulers.parallel()))
                .log();
    }

    private String upperCaseWithDelay(String name) {
        delay(1000);
        return name.toUpperCase();
    }

}
