package com.maherguru.redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.RPatternTopicReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.api.listener.PatternMessageListener;
import org.redisson.client.codec.StringCodec;

// Test class for demonstrating reactive Redis pub/sub functionality
public class Lec12PubSubTest extends BaseTest {

    // Test method for subscriber 1 using a specific topic
    @Test
    public void subscriber1(){
        // Get a reactive topic for the specified key "slack-room1"
        RTopicReactive topic = this.client.getTopic("slack-room1", StringCodec.INSTANCE);

        // Subscribe to messages from the topic, print messages, and handle errors
        topic.getMessages(String.class)
                .doOnError(System.out::println)
                .doOnNext(System.out::println)
                .subscribe();

        // Sleep for 10 minutes (600,000 milliseconds) to keep the test running
        sleep(600_000);
    }

    // Test method for subscriber 2 using a pattern-based topic
    @Test
    public void subscriber2(){
        // Get a reactive pattern topic for keys matching the pattern "slack-room*"
        RPatternTopicReactive patternTopic = this.client.getPatternTopic("slack-room*", StringCodec.INSTANCE);

        // Add a listener for messages on the pattern topic and print details
        patternTopic.addListener(String.class, new PatternMessageListener<String>() {
            @Override
            public void onMessage(CharSequence pattern, CharSequence topic, String msg) {
                System.out.println(pattern + " : " + topic + " : " + msg);
            }
        }).subscribe();

        // Sleep for 10 minutes (600,000 milliseconds) to keep the test running
        sleep(600_000);
    }
}