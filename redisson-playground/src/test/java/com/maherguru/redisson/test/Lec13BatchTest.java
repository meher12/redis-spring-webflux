package com.maherguru.redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.BatchOptions;
import org.redisson.api.RBatchReactive;
import org.redisson.api.RListReactive;
import org.redisson.api.RSetReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

// Test class for demonstrating batch operations in Redis
public class Lec13BatchTest extends BaseTest {

    // Test method for batch operations
    @Test
    public void batchTest(){
        // Create a reactive batch with default options
        RBatchReactive batch = this.client.createBatch(BatchOptions.defaults());

        // Get a reactive list and set from the batch
        RListReactive<Long> list = batch.getList("numbers-list", LongCodec.INSTANCE);
        RSetReactive<Long> set = batch.getSet("numbers-set", LongCodec.INSTANCE);

        // Add elements to the list and set in the batch (500,000 elements)
        for (long i = 0; i < 500_000; i++) {
            list.add(i);
            set.add(i);
        }

        // Execute the batch and verify completion using StepVerifier
        StepVerifier.create(batch.execute().then())
                .verifyComplete();
    }

    // Test method for regular (non-batch) operations
    @Test
    public void regularTest(){
        // Get a reactive list and set directly from the Redis client
        RListReactive<Long> list = this.client.getList("numbers-list", LongCodec.INSTANCE);
        RSetReactive<Long> set = this.client.getSet("numbers-set", LongCodec.INSTANCE);

        // Create a Mono<Void> representing the completion of the operations
        Mono<Void> mono = Flux.range(1, 500_000)
                .map(Long::valueOf)
                .flatMap(i -> list.add(i).then(set.add(i)))
                .then();

        // Verify completion of the operations using StepVerifier
        StepVerifier.create(mono)
                .verifyComplete();
    }
}