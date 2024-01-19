package com.maherguru.redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.DeletedObjectListener;
import org.redisson.api.ExpiredObjectListener;
import org.redisson.api.RBucketReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class Lec05EventListenerTest extends BaseTest {

    // Test method for expired event with an ExpiredObjectListener
    @Test
    public void expiredEventTest() {
        // Create a reactive bucket with key "user:1:name" and value type String
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);

        // Set the value of the bucket to "sam" with an expiry time of 10 seconds
        Mono<Void> set = bucket.set("sam", Duration.ofSeconds(10));

        // Retrieve the value of the bucket and print it
        Mono<Void> get = bucket.get()
                .doOnNext(System.out::println)
                .then();

        // Add an ExpiredObjectListener to the bucket that prints a message when the bucket expires
        Mono<Void> event = bucket.addListener(new ExpiredObjectListener() {
            @Override
            public void onExpired(String s) {
                System.out.println("Expired : " + s);
            }
        }).then();

        // Verify that setting the value, getting the value, and adding the listener all complete successfully
        StepVerifier.create(set.concatWith(get).concatWith(event))
                .verifyComplete();

        // Pause execution for 11 seconds to allow the bucket to expire
        sleep(11000);
    }

    // Test method for deleted event with a DeletedObjectListener
    @Test
    public void deletedEventTest() {
        // Create a reactive bucket with key "user:1:name" and value type String
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);

        // Set the value of the bucket to "sam"
        Mono<Void> set = bucket.set("sam");

        // Retrieve the value of the bucket and print it
        Mono<Void> get = bucket.get()
                .doOnNext(System.out::println)
                .then();

        // Add a DeletedObjectListener to the bucket that prints a message when the bucket is deleted
        Mono<Void> event = bucket.addListener(new DeletedObjectListener() {
            @Override
            public void onDeleted(String s) {
                System.out.println("Deleted : " + s);
            }
        }).then();

        // Verify that setting the value, getting the value, and adding the listener all complete successfully
        StepVerifier.create(set.concatWith(get).concatWith(event))
                .verifyComplete();

        // Pause execution for 60 seconds to allow the bucket to be deleted
        sleep(60000);
    }

}
