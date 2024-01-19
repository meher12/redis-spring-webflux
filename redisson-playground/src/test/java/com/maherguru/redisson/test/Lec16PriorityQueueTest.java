package com.maherguru.redisson.test;

import com.maherguru.redisson.assignment.Category;
import com.maherguru.redisson.assignment.PriorityQueue;
import com.maherguru.redisson.assignment.UserOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

// Test class for demonstrating reactive Redis Priority Queue
public class Lec16PriorityQueueTest extends BaseTest {

    // Priority queue instance using a scored sorted set
    private PriorityQueue priorityQueue;

    // Setup method to initialize the priority queue before running tests
    @BeforeAll
    public void setupQueue(){
        // Get a reactive scored sorted set from Redis with key "user:order:queue"
        RScoredSortedSetReactive<UserOrder> sortedSet = this.client.getScoredSortedSet("user:order:queue", new TypedJsonJacksonCodec(UserOrder.class));

        // Create a priority queue instance using the scored sorted set
        this.priorityQueue = new PriorityQueue(sortedSet);
    }

    // Test method for the producer (adding orders to the priority queue)
    @Test
    public void producer(){
        // Generate a Flux emitting values every second
        Flux.interval(Duration.ofSeconds(1))
                .map(l -> (l.intValue() * 5))
                .doOnNext(i -> {
                    // Create UserOrder instances with different categories
                    UserOrder u1 = new UserOrder(i + 1, Category.GUEST);
                    UserOrder u2 = new UserOrder(i + 2, Category.STD);
                    UserOrder u3 = new UserOrder(i + 3, Category.PRIME);
                    UserOrder u4 = new UserOrder(i + 4, Category.STD);
                    UserOrder u5 = new UserOrder(i + 5, Category.GUEST);

                    // Add UserOrder instances to the priority queue
                    Mono<Void> mono = Flux.just(u1, u2, u3, u4, u5)
                            .flatMap(this.priorityQueue::add)
                            .then();

                    // Verify completion of the addition using StepVerifier
                    StepVerifier.create(mono)
                            .verifyComplete();
                }).subscribe();

        // Sleep for 1 minute (60,000 milliseconds) to allow asynchronous operations to complete
        sleep(60_000);
    }

    // Test method for the consumer (taking items from the priority queue)
    @Test
    public void consumer(){
        // Take items from the priority queue, delay each emission by 500 milliseconds, and print them
        this.priorityQueue.takeItems()
                .delayElements(Duration.ofMillis(500))
                .doOnNext(System.out::println)
                .subscribe();

        // Sleep for 10 minutes (600,000 milliseconds) to allow asynchronous operations to complete
        sleep(600_000);
    }
}
