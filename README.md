# Course Structure
This course is divided into several key sections, each delving into a different aspect of Redis and its applications. Here's what you can expect:

## Section 1: Introduction to Redis
This section offers a crash course on Redis. Our primary focus will be on using the redis-cli to understand various Redis commands. We’ll be managing these command lines in redis-cli through a Redis Docker image.  

## Section 2: Redisson - A Redis Java Client
In this section, we introduce [Redisson](https://github.com/redisson/redisson/wiki/Table-of-Content), a Redis Java client.
- The first step is to create `redisson-playground` project and all the necessary dependencies
- Create `RedissonConfig` and BaseTest classes in `com.maherguru.redisson.test.*` test packages
- [Data serialization with redisson](https://github.com/redisson/redisson/wiki/4.-data-serialization) there is many popular codecs are available for usage `StringCodec`, `TypedJsonJacksonCodec` ... 
1. Playing with `Lec01KeyValueTest` class:
   - Test method for basic key-value access
   - Test method for key-value access with expiration
   - Test method for key-value access with extending expiry time
2. Playing with `Lec02KeyValueObjectTest` class:
   - Test method for key-value access with a custom object (Student)
3. Playing with `Lec03NumberTest` class:
   - Test method for key-value access with atomic long increment
4. Playing with `Lec04BucketAsMapTest` class:
   - Test method for retrieving values from multiple buckets as a map
5. Playing with `Lec05EventListenerTest` class:
   - Test method for expired event with an ExpiredObjectListener:
     * We might need this command to get the expired events from Redis
       ``` 
         # https://redis.io/docs/manual/keyspace-notifications/
         config set notify-keyspace-events AKE 
         # Output in console:
           sam
           Expired : user:1:name
       ```
   - Test method for deleted event with a DeletedObjectListener
6. Playing with `Lec06MapTest` class:
   - Test method for putting individual values into a map
   - Test method for putting a Java Map into a map
   - Test method for putting objects into a map with a custom codec
7. Playing with `Lec07MapCacheTest` class:
   - Test method for using a map cache with a custom codec
8. Playing with `Lec08LocalCachedMapTest` class:
   - Test method to simulate an application server updating the map periodically
   - Test method to simulate another application server updating the map
9. Playing with `Lec09ListQueueStackTest` class:
   - Test method for interacting with a Redis list
   - Test method for interacting with a Redis queue
   - Test method for interacting with a Redis stack (deque)
10. Playing with `Lec10MessageQueueTest` class:
    - Test method for msgQueue 1
    - Test method for msgQueue 2
11. Playing with `Lec11HyperLogLogTest` class:
    - Test method to demonstrate counting unique elements using HyperLogLog
12. Playing with `Lec12PubSubTest` class:
    - Test method for subscriber 1 using a specific topic
    - Test method for subscriber 2 using a pattern-based topic
13. Playing with `Lec13BatchTest` class:
    - Test method for batch operations
    - Test method for regular (non-batch) operations
14. Playing with `Lec14TransactionTest` class:
    - Test method for a non-transactional transfer (with a deliberate error) 
    - Test method for a transactional transfer
15. Playing with `Lec15SortedSetTest` class:
    - Test method for a simple sorted set operation
    - Test method for using a batch to add scores to a sorted set
16. Playing with `Lec16PriorityQueueTest` class:
    - Test method for the producer (adding orders to the priority queue)
    - Test method for the consumer (taking items from the priority queue) 
17. Playing with `Lec17GeoSpatialTest` class:
    - Test method for adding restaurant locations to Geo and Map
    - Test method for searching for nearby locations based on a given GeoLocation 


