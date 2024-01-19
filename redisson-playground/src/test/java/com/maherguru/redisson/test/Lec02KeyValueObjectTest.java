package com.maherguru.redisson.test;

import com.maherguru.redisson.dto.Student;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

public class Lec02KeyValueObjectTest extends BaseTest {

    // Test method for key-value access with a custom object (Student)
    @Test
    public void keyValueObjectTest(){
        // Create a new Student object with name "maher", age 10, city "ariana", and grades [1, 2, 3]
        Student student = new Student("maher", 10, "ariana", Arrays.asList(1,2, 3));

        // Create a reactive bucket with key "student:1" and value type Student using Jackson JSON codec
        RBucketReactive<Student> bucket = this.client.getBucket("student:1", new TypedJsonJacksonCodec(Student.class));

        // Set the value of the bucket to the Student object
        Mono<Void> set = bucket.set(student);

        // Retrieve the value of the bucket and print it
        Mono<Void> get = bucket.get()
                .doOnNext(System.out::println)
                .then();

        // Verify that setting and getting the value completes successfully
        StepVerifier.create(set.concatWith(get))
                .verifyComplete();
    }

}
