package com.maherguru.redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class Lec01KeyValueTest extends BaseTest{


    // Test method for basic key-value access
    @Test
    public void keyValueAccessTest() {
        // Obtain a reactive Redis bucket for key "user:1:name" and a string codec of org.redisson.client.codec.StringCodec
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);

        // Asynchronously set the value "sam" in the Redis bucket
        Mono<Void> set = bucket.set("sam");

        // Asynchronously get the value from the Redis bucket, print it, and ensure completion
        Mono<Void> get = bucket.get()
                .doOnNext(System.out::println)
                .then();

        // Verify that both set and get operations complete successfully
        StepVerifier.create(set.concatWith(get))
                .verifyComplete();
    }

    // Test method for key-value access with expiration
    @Test
    public void keyValueExpiryTest() {
        // Obtain a reactive Redis bucket for key "user:1:name" and a string codec
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);

        // Asynchronously set the value "sam" in the Redis bucket with a 10-second expiration
        Mono<Void> set = bucket.set("sam", Duration.ofSeconds(10));

        // Asynchronously get the value from the Redis bucket, print it, and ensure completion
        Mono<Void> get = bucket.get()
                .doOnNext(System.out::println)
                .then();

        // Verify that both set and get operations complete successfully
        StepVerifier.create(set.concatWith(get))
                .verifyComplete();
    }

    // Test method for key-value access with extending expiry time
    @Test
    public void keyValueExtendExpiryTest() {
        // Create a reactive bucket with key "user:1:name" and value type String
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);

        // Set the value of the bucket to "sam" with an expiry time of 10 seconds
        Mono<Void> set = bucket.set("sam", Duration.ofSeconds(10));

        // Retrieve the value of the bucket and print it
        Mono<Void> get = bucket.get()
                .doOnNext(System.out::println)
                .then();

        // Verify that setting and getting the value completes successfully
        StepVerifier.create(set.concatWith(get))
                .verifyComplete();

        // Pause execution for 5 seconds
        sleep(5000);

        // Extend the expiry time of the bucket to 60 seconds
        Mono<Boolean> mono = bucket.expire(Duration.ofSeconds(60));

        // Verify that extending the expiry time completes successfully and returns true
        StepVerifier.create(mono)
                .expectNext(true)
                .verifyComplete();

        // Access the remaining time to live of the bucket and print it
        Mono<Void> ttl = bucket.remainTimeToLive()
                .doOnNext(System.out::println)
                .then();

        // Verify that accessing the remaining time to live completes successfully
        StepVerifier.create(ttl)
                .verifyComplete();
    }
}
