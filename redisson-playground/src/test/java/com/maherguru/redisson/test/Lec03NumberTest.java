package com.maherguru.redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLongReactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public class Lec03NumberTest extends BaseTest {

    // Test method for key-value access with atomic long increment
    @Test
    public void keyValueIncreaseTest() {
        // Obtain a reactive atomic long with key "user:1:visit"
        RAtomicLongReactive atomicLong = this.client.getAtomicLong("user:1:visit");

        // Increment the atomic long value 30 times with a delay of 1 second between increments
        Mono<Void> mono = Flux.range(1, 30)
                .delayElements(Duration.ofSeconds(1))
                .flatMap(i -> atomicLong.incrementAndGet())
                .then();

        // Verify that the increment operations complete successfully
        StepVerifier.create(mono)
                .verifyComplete();
    }

}
