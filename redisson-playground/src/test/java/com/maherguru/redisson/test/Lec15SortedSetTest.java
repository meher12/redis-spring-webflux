package com.maherguru.redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.BatchOptions;
import org.redisson.api.RBatchReactive;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Map;
import java.util.function.Function;

// Test class for demonstrating reactive Redis sorted sets
public class Lec15SortedSetTest extends BaseTest {

    // Test method for a simple sorted set operation
    @Test
    public void sortedSet(){
        // Get a reactive scored sorted set from Redis with key "student:score"
        RScoredSortedSetReactive<String> sortedSet = this.client.getScoredSortedSet("student:score", StringCodec.INSTANCE);

        // Add scores and values to the sorted set and verify completion
        Mono<Void> mono = sortedSet.addScore("sam", 12.25)
                .then(sortedSet.add(23.25, "mike"))
                .then(sortedSet.addScore("jake", 7))
                .then();

        // Verify completion of the operations using StepVerifier
        StepVerifier.create(mono)
                .verifyComplete();

        // Retrieve and print the top entry from the sorted set
        sortedSet.entryRange(0, 1)
                .flatMapIterable(Function.identity()) // convert flux to iterable
                .map(se -> se.getScore() + " : " + se.getValue())
                .doOnNext(System.out::println)
                .subscribe();

        // Sleep for 1 second to allow asynchronous operations to complete
        sleep(1000);
    }

    // Test method for using a batch to add scores to a sorted set
    @Test
    public void test(){
        // Get a reactive scored sorted set from Redis with key "prod:score"
        RScoredSortedSetReactive<String> sortedSet = this.client.getScoredSortedSet("prod:score", StringCodec.INSTANCE);

        // Create a map with values and scores
        Map<String, Long> a = Map.of(
                "a", 10L,
                "b", 15L
        );

        // Create a reactive batch with default options
        RBatchReactive batch = this.client.createBatch(BatchOptions.defaults());

        // Get a scored sorted set from the batch
        RScoredSortedSetReactive<String> set = batch.getScoredSortedSet("prod:score", StringCodec.INSTANCE);

        // Use Flux to add scores and values to the batch
        Flux.fromIterable(a.entrySet())
                .doFinally(s -> System.out.println("done1"))
                .doOnNext(System.out::println)
                .map(e -> set.addScore(e.getKey(), e.getValue()))
                .then(batch.execute())
                .doOnNext(r -> System.out.println(r.getResponses()))
                .subscribe();

        // Use Flux to print intervals of time (buffers every second)
        Flux.interval(Duration.ofSeconds(3))
                .buffer(Duration.ofSeconds(1))
                .subscribe(System.out::println);

        // Sleep for 10 seconds to allow asynchronous operations to complete
        sleep(10_000);
    }
}