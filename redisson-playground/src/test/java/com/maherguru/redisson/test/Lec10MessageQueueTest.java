package com.maherguru.redisson.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBlockingDequeReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public class Lec10MessageQueueTest extends BaseTest {

    // Declare a reactive blocking deque for Long elements
    private RBlockingDequeReactive<Long> msgQueue;

    // Setup method to initialize the msgQueue before running tests
    @BeforeAll
    public void setupQueue(){
        // Get a reactive blocking deque with Long elements from Redis using key "message-queue"
        this.msgQueue = this.client.getBlockingDeque("message-queue", LongCodec.INSTANCE);
    }

    // Test method for Consumer 1
    @Test
    public void consumer1(){
        // Subscribe to takeElements from the msgQueue, print consumed elements, handle errors
        this.msgQueue.takeElements()
                .doOnNext(i -> System.out.println("Consumer 1 : " + i))
                .doOnError(System.out::println)
                .subscribe();

        // Sleep for 10 minutes (600,000 milliseconds) to keep the test running
        sleep(600_000);
    }

    // Test method for Consumer 2
    @Test
    public void consumer2(){
        // Subscribe to takeElements from the msgQueue, print consumed elements, handle errors
        this.msgQueue.takeElements()
                .doOnNext(i -> System.out.println("Consumer 2 : " + i))
                .doOnError(System.out::println)
                .subscribe();

        // Sleep for 10 minutes (600,000 milliseconds) to keep the test running
        sleep(600_000);
    }

    // Test method for Producer
    @Test
    public void producer(){
        // Create a Mono<Void> that represents the completion of the producer operation
        Mono<Void> mono = Flux.range(1, 100)
                .delayElements(Duration.ofMillis(500))
                .doOnNext(i -> System.out.println("going to add " + i))
                .flatMap(i -> this.msgQueue.add(Long.valueOf(i)))
                .then();

        // Verify completion of the producer operation using StepVerifier
        StepVerifier.create(mono)
                .verifyComplete();
    }


}
