package com.maherguru.redisson.test;

import com.maherguru.redisson.dto.Student;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapCacheReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Lec07MapCacheTest extends BaseTest {

    // Test method for using a map cache with a custom codec
    @Test
    public void mapCacheTest() {
        // Create a custom codec for serializing/deserializing a map with Integer keys and Student values
        // Map<Integer, Student>
        TypedJsonJacksonCodec codec = new TypedJsonJacksonCodec(Integer.class, Student.class);

        // Obtain a reactive map cache with key "users:cache" and value type Map<Integer, Student>
        RMapCacheReactive<Integer, Student> mapCache = this.client.getMapCache("users:cache", codec);

        // Create two Student objects
        Student student1 = new Student("sam", 10, "atlanta", List.of(1, 2, 3));
        Student student2 = new Student("jake", 30, "miami", List.of(10, 20, 30));

        // Put the Student objects into the map cache with keys 1 and 2, each with a specified expiration time
        Mono<Student> st1 = mapCache.put(1, student1, 5, TimeUnit.SECONDS);
        Mono<Student> st2 = mapCache.put(2, student2, 10, TimeUnit.SECONDS);

        // Verify that putting Student objects into the map cache completes successfully
        StepVerifier.create(st1.then(st2).then())
                .verifyComplete();

        // Pause execution for 3 seconds
        sleep(3000);

        // Access students with keys 1 and 2 from the map cache and print them
        mapCache.get(1).doOnNext(System.out::println).subscribe();
        mapCache.get(2).doOnNext(System.out::println).subscribe();

        // Pause execution for another 3 seconds
        sleep(3000);

        // Access students again with keys 1 and 2 from the map cache and print them
        mapCache.get(1).doOnNext(System.out::println).subscribe();
        mapCache.get(2).doOnNext(System.out::println).subscribe();
    }


}