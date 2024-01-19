package com.maherguru.redisson.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RTransactionReactive;
import org.redisson.api.TransactionOptions;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

// Test class for demonstrating reactive Redis transactions
public class Lec14TransactionTest extends BaseTest {

    // Reactive buckets for user balances
    private RBucketReactive<Long> user1Balance;
    private RBucketReactive<Long> user2Balance;

    // Setup method to initialize user balances before running tests
    @BeforeAll
    public void accountSetup(){
        // Get reactive buckets for user balances and set initial balances
        this.user1Balance = this.client.getBucket("user:1:balance", LongCodec.INSTANCE);
        this.user2Balance = this.client.getBucket("user:2:balance", LongCodec.INSTANCE);
        Mono<Void> mono = user1Balance.set(100L)
                .then(user2Balance.set(0L))
                .then();

        // Verify completion of the setup using StepVerifier
        StepVerifier.create(mono)
                .verifyComplete();
    }

    // Cleanup method to print the final user balance status after running tests
    @AfterAll
    public void accountBalanceStatus(){
        // Get the user balances and print them
        Mono<Void> mono = Flux.zip(this.user1Balance.get(), this.user2Balance.get())
                .doOnNext(System.out::println)
                .then();

        // Verify completion of the status check using StepVerifier
        StepVerifier.create(mono)
                .verifyComplete();
    }

    // Test method for a non-transactional transfer (with a deliberate error)
    @Test
    public void nonTransactionTest(){
        // Perform a transfer and introduce an error (divide by zero)
        this.transfer(user1Balance, user2Balance, 50)
                .thenReturn(0)
                .map(i -> (5 / i)) // some error
                .doOnError(System.out::println)
                .subscribe();

        // Sleep for 1 second to allow asynchronous operations to complete
        sleep(1000);
    }

    // Test method for a transactional transfer
    @Test
    public void transactionTest(){
        // Create a reactive transaction with default options
        RTransactionReactive transaction = this.client.createTransaction(TransactionOptions.defaults());

        // Get reactive buckets for user balances within the transaction
        RBucketReactive<Long> user1Balance = transaction.getBucket("user:1:balance", LongCodec.INSTANCE);
        RBucketReactive<Long> user2Balance = transaction.getBucket("user:2:balance", LongCodec.INSTANCE);

        // Perform a transfer within the transaction, handle errors, and commit or rollback the transaction
        this.transfer(user1Balance, user2Balance, 50)
                .thenReturn(0)
                .map(i -> (5 / i)) // some error
                .then(transaction.commit())
                .doOnError(System.out::println)
                .onErrorResume(ex -> transaction.rollback())
                .subscribe();

        // Sleep for 1 second to allow asynchronous operations to complete
        sleep(1000);
    }

    // Helper method for transferring an amount from one account to another
    private Mono<Void> transfer(RBucketReactive<Long> fromAccount, RBucketReactive<Long> toAccount, int amount){
        return Flux.zip(fromAccount.get(), toAccount.get()) // [b1, b2]
                .filter(t -> t.getT1() >= amount)
                .flatMap(t -> fromAccount.set(t.getT1() - amount).thenReturn(t))
                .flatMap(t -> toAccount.set(t.getT2() + amount))
                .then();
    }
}
