package com.maherguru.redisson.test;

import com.maherguru.redisson.test.config.RedissonConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.redisson.api.RedissonReactiveClient;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {

    private final RedissonConfig redissonConfig = new RedissonConfig();
    protected RedissonReactiveClient client;

    @BeforeAll
    public void setClient(){
        this.client = this.redissonConfig.getReactiveClient();
    }

    @AfterAll
    public void shutdown(){
        this.client.shutdown();
    }

    protected void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
