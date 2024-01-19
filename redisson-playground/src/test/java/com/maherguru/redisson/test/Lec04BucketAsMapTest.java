package com.maherguru.redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec04BucketAsMapTest extends BaseTest {
    // user:1:name
    // user:2:name
    // user:3:name

    // Test method for retrieving values from multiple buckets as a map
    @Test
    public void bucketsAsMap() {
        // Obtain a reactive RBuckets instance using StringCodec for encoding/decoding values
        Mono<Void> mono = this.client.getBuckets(StringCodec.INSTANCE)
        // Retrieve values from multiple buckets with keys "user:1:name", "user:2:name", "user:3:name", "user:4:name"
                .get("user:1:name", "user:2:name", "user:3:name", "user:4:name")
                .doOnNext(System.out::println)
                .then();

        // Verify that the retrieval of values from buckets completes successfully
        StepVerifier.create(mono)
                .verifyComplete();
    }

}
