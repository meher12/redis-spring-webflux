package com.maherguru.redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.RDequeReactive;
import org.redisson.api.RListReactive;
import org.redisson.api.RQueueReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Lec09ListQueueStackTest extends BaseTest {

    // Test method for interacting with a Redis list
    @Test
    public void listTest() {
        // Get a reactive list with Long elements from Redis using key "number-input"
        RListReactive<Long> list = this.client.getList("number-input", LongCodec.INSTANCE);

        // Create a List of Long from a range of values (1 to 10)
        List<Long> longList = LongStream.rangeClosed(1, 10)
                .boxed()
                .collect(Collectors.toList());

        // Add all elements from longList to the Redis list and verify completion
        StepVerifier.create(list.addAll(longList).then())
                .verifyComplete();

        // Verify that the size of the Redis list is 10
        StepVerifier.create(list.size())
                .expectNext(10)
                .verifyComplete();
    }

    // Test method for interacting with a Redis queue
    @Test
    public void queueTest() {
        // Get a reactive queue with Long elements from Redis using key "number-input"
        RQueueReactive<Long> queue = this.client.getQueue("number-input", LongCodec.INSTANCE);

        // Poll elements from the queue three times, print them, and then verify completion
        Mono<Void> queuePoll = queue.poll()
                .repeat(3)
                .doOnNext(System.out::println)
                .then();

        // Verify completion of the queuePoll operation
        StepVerifier.create(queuePoll)
                .verifyComplete();

        // Verify that the size of the Redis queue is 6
        StepVerifier.create(queue.size())
                .expectNext(6)
                .verifyComplete();
    }

    // Test method for interacting with a Redis stack (deque)
    @Test
    public void stackTest() {
        // Get a reactive deque (stack) with Long elements from Redis using key "number-input"
        RDequeReactive<Long> deque = this.client.getDeque("number-input", LongCodec.INSTANCE);

        // Poll elements from the end of the deque (stack) three times, print them, and then verify completion
        Mono<Void> stackPoll = deque.pollLast()
                .repeat(3)
                .doOnNext(System.out::println)
                .then();

        // Verify completion of the stackPoll operation
        StepVerifier.create(stackPoll)
                .verifyComplete();

        // Verify that the size of the Redis deque (stack) is 2
        StepVerifier.create(deque.size())
                .expectNext(2)
                .verifyComplete();
    }
}

