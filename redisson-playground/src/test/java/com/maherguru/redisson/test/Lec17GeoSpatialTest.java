package com.maherguru.redisson.test;

import com.maherguru.redisson.dto.GeoLocation;
import com.maherguru.redisson.dto.Restaurant;
import com.maherguru.redisson.util.RestaurantUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.GeoUnit;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

// Test class for demonstrating reactive Redis GeoSpatial features
public class Lec17GeoSpatialTest extends BaseTest {

    // Reactive Geo instance for managing restaurant locations
    private RGeoReactive<Restaurant> geo;

    // Reactive Map instance for storing GeoLocations of places in Texas
    private RMapReactive<String, GeoLocation> map;

    // Setup method to initialize Geo and Map instances before running tests
    @BeforeAll
    public void setGeo(){
        // Get a reactive Geo instance for key "restaurants" with a typed codec for Restaurant class
        this.geo = this.client.getGeo("restaurants", new TypedJsonJacksonCodec(Restaurant.class));

        // Get a reactive Map instance for key "us:texas" with typed codecs for String and GeoLocation classes
        this.map = this.client.getMap("us:texas", new TypedJsonJacksonCodec(String.class, GeoLocation.class));
    }

    // Test method for adding restaurant locations to Geo and Map
    @Test
    public void add(){
        // Create a Mono<Void> by adding restaurant locations to Geo and Map
        Mono<Void> mono = Flux.fromIterable(RestaurantUtil.getRestaurants())
                .flatMap(r -> this.geo.add(r.getLongitude(), r.getLatitude(), r).thenReturn(r))
                .flatMap(r -> this.map.fastPut(r.getZip(), GeoLocation.of(r.getLongitude(), r.getLatitude())))
                .then();

        // Verify completion of the addition using StepVerifier
        StepVerifier.create(mono)
                .verifyComplete();
    }

    // Test method for searching for nearby locations based on a given GeoLocation
    @Test
    public void search(){
        // Create a Mono<Void> by searching for nearby locations based on a GeoLocation in the map
        Mono<Void> mono = this.map.get("75224")
                .map(gl -> GeoSearchArgs.from(gl.getLongitude(), gl.getLatitude()).radius(5, GeoUnit.MILES))
                .flatMap(r -> this.geo.search(r))
                .flatMapIterable(Function.identity())
                .doOnNext(System.out::println)
                .then();

        // Verify completion of the search using StepVerifier
        StepVerifier.create(mono)
                .verifyComplete();
    }
}