package com.maherguru.redisson.test;

import com.maherguru.redisson.dto.Student;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapReactive;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

public class Lec06MapTest extends BaseTest {


    // Test method for putting individual values into a map
    @Test
    public void mapTest1() {
        // Obtain a reactive map with key "user:1" and value type String
        RMapReactive<String, String> map = this.client.getMap("user:1", StringCodec.INSTANCE);

        // Put individual values into the map for keys "name", "age", and "city"
        Mono<String> name = map.put("name", "sam");
        Mono<String> age = map.put("age", "10");
        Mono<String> city = map.put("city", "atlanta");

        // Verify that putting values into the map completes successfully
        StepVerifier.create(name.concatWith(age).concatWith(city).then())
                .verifyComplete();
    }

    // Test method for putting a Java Map into a map
    @Test
    public void mapTest2() {
        // Obtain a reactive map with key "user:2" and value type String
        RMapReactive<String, String> map = this.client.getMap("user:2", StringCodec.INSTANCE);

        // Create a Java Map with key-value pairs
        Map<String, String> javaMap = Map.of(
                "name", "jake",
                "age", "30",
                "city", "miami"
        );

        // Put the entire Java Map into the map
        StepVerifier.create(map.putAll(javaMap).then())
                .verifyComplete();
    }

    // Test method for putting objects into a map with a custom codec
    @Test
    public void mapTest3() {
        // Create a custom codec for serializing/deserializing a map with Integer keys and Student values
        TypedJsonJacksonCodec codec = new TypedJsonJacksonCodec(Integer.class, Student.class);

        // Obtain a reactive map with key "users" and value type Map<Integer, Student>
        RMapReactive<Integer, Student> map = this.client.getMap("users", codec);

        // Create two Student objects
        Student student1 = new Student("sam", 10, "atlanta", List.of(1, 2, 3));
        Student student2 = new Student("jake", 30, "miami", List.of(10, 20, 30));

        // Put the Student objects into the map with keys 1 and 2
        Mono<Student> mono1 = map.put(1, student1);
        Mono<Student> mono2 = map.put(2, student2);

        // Verify that putting Student objects into the map completes successfully
        StepVerifier.create(mono1.concatWith(mono2).then())
                .verifyComplete();
    }
}
