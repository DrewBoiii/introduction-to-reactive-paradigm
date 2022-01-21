package com.learnreactiveprogramming;

import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

public class ColdAndHotPublisherTest {

    @Test
    void coldPublisherTest() {
        var flux = Flux.range(1, 5);

        flux.subscribe(integer -> System.out.printf("Number of subscriber 1 is %s\n", integer));
        flux.subscribe(integer -> System.out.printf("Number of subscriber 2 is %s\n", integer));
    }

    @Test
    void hotPublisherTest() {
        var flux = Flux.range(1, 5)
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<Integer> connectableFlux = flux.publish();
        connectableFlux.connect();

        connectableFlux.subscribe(integer -> System.out.printf("Number of subscriber 1 is %s\n", integer));

        delay(3000);

        connectableFlux.subscribe(integer -> System.out.printf("Number of subscriber 2 is %s\n", integer));

        delay(5000);
    }

    @Test
    void hotPublisherTest_autoConnect() {
        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1));

        var hotSource = flux.publish().autoConnect(2); // going to wait at least 2 subs until executed

        hotSource.subscribe(integer -> System.out.println("S 1 - " + integer));
        delay(2000);

        hotSource.subscribe(integer -> System.out.println("S 2 - " + integer));
        delay(2000);

        hotSource.subscribe(integer -> System.out.println("S 3 - " + integer));
        delay(10000);
    }

    @Test
    void hotPublisherTest_refCount() {
        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1))
                .doOnCancel(() -> System.out.println("Cancel signal"));

        var hotSource = flux.publish().refCount(2); // going to wait at least 2 subs until executed

        Disposable disposable1 = hotSource.subscribe(integer -> System.out.println("S 1 - " + integer));
        delay(2000);

        Disposable disposable2 = hotSource.subscribe(integer -> System.out.println("S 2 - " + integer));
        delay(2000);

        disposable1.dispose();
        disposable2.dispose();

        hotSource.subscribe(integer -> System.out.println("S 3 - " + integer));
        delay(2000);

        hotSource.subscribe(integer -> System.out.println("S 4 - " + integer));

        delay(10000);
    }

}
