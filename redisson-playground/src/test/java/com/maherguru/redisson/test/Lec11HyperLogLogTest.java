package com.maherguru.redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.RHyperLogLogReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Lec11HyperLogLogTest extends BaseTest {

    // Test method to demonstrate counting unique elements using HyperLogLog
    @Test
    public void count(){
        // Get a reactive HyperLogLog counter from Redis using key "user:visits"
        RHyperLogLogReactive<Long> counter = this.client.getHyperLogLog("user:visits", LongCodec.INSTANCE);

        // Create four lists of Long elements with different ranges
        List<Long> list1 = LongStream.rangeClosed(1, 25000)
                .boxed()
                .collect(Collectors.toList());

        List<Long> list2 = LongStream.rangeClosed(25001, 50000)
                .boxed()
                .collect(Collectors.toList());

        List<Long> list3 = LongStream.rangeClosed(1, 75000)
                .boxed()
                .collect(Collectors.toList());

        List<Long> list4 = LongStream.rangeClosed(50000, 100_000)
                .boxed()
                .collect(Collectors.toList());

        // Combine the four lists into a Flux and add them to the HyperLogLog counter
        Mono<Void> mono = Flux.just(list1, list2, list3, list4)
                .flatMap(counter::addAll)
                .then();

        // Verify completion of the operation using StepVerifier
        StepVerifier.create(mono)
                .verifyComplete();

        // Get and print the count of unique elements in the HyperLogLog
        counter.count()
                .doOnNext(System.out::println)
                .subscribe();
    }


}