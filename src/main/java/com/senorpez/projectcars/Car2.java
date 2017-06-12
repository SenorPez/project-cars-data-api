package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

import java.util.concurrent.atomic.AtomicInteger;

@Relation(value = "car", collectionRelation = "car")
class Car2 implements Identifiable<Integer> {
    @JsonProperty("id")
    private final Integer id;
    @JsonProperty("year")
    private final Integer year;
    @JsonProperty("manufacturer")
    private final String manufacturer;
    @JsonProperty("model")
    private final String model;
    @JsonProperty("verified")
    private final Boolean verified;
    @JsonProperty("dlc")
    private final String dlc;

    private static AtomicInteger idCounter = new AtomicInteger(0);

    @JsonCreator
    public Car2(
            @JsonProperty("year") final Integer year,
            @JsonProperty("manufacturer") final String manufacturer,
            @JsonProperty("model") final String model,
            @JsonProperty("verified") final Boolean verified,
            @JsonProperty("dlc") final String dlc) {
        this.id = idCounter.getAndIncrement();
        this.year = year;
        this.manufacturer = manufacturer;
        this.model = model;
        this.verified = verified;
        this.dlc = dlc;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
