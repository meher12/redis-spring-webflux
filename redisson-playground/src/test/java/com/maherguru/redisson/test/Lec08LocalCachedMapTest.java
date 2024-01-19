package com.maherguru.redisson.test;

import com.maherguru.redisson.dto.Student;
import com.maherguru.redisson.test.config.RedissonConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

public class Lec08LocalCachedMapTest extends BaseTest {

    // Declare a private variable to hold the local cached map of students
    private RLocalCachedMap<Integer, Student> studentsMap;

    // Method to set up the Redisson client and create a local cached map
    @BeforeAll
    public void setupClient(){
        // Create a Redisson configuration object
        RedissonConfig config = new RedissonConfig();

        // Get a Redisson client from the configuration
        RedissonClient redissonClient = config.getClient();

        // Set up options for the local cached map
        LocalCachedMapOptions<Integer, Student> mapOptions = LocalCachedMapOptions.<Integer, Student>defaults()
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.NONE);

        // Get a local cached map from the Redisson client and store it in the studentsMap variable
        this.studentsMap = redissonClient.getLocalCachedMap(
                "students",
                new TypedJsonJacksonCodec(Integer.class, Student.class),
                mapOptions
        );
    }

    // Test method to simulate an application server updating the map periodically
    @Test
    public void appServer1(){
        // Create two Student objects
        Student student1 = new Student("sam", 10, "atlanta", List.of(1, 2, 3));
        Student student2 = new Student("jake", 30, "miami", List.of(10, 20, 30));

        // Put the Student objects into the local cached map with keys 1 and 2
        this.studentsMap.put(1, student1);
        this.studentsMap.put(2, student2);

        // Create a Flux that emits a value every second
        // For each value emitted, print the value and the Student object with key 1 from the local cached map
        Flux.interval(Duration.ofSeconds(1))
                .doOnNext(i -> System.out.println(i + " ==> " + studentsMap.get(1)))
                .subscribe();

        // Pause execution for 600000 milliseconds (10 minutes)
        sleep(600000);
    }

    // Test method to simulate another application server updating the map
    @Test
    public void appServer2(){
        // Create a new Student object
        Student student1 = new Student("sam-updated", 10, "atlanta", List.of(1, 2, 3));

        // Update the Student object with key 1 in the local cached map
        this.studentsMap.put(1, student1);
    }

}