package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

import java.util.concurrent.atomic.AtomicInteger;

@Relation(value = "car", collectionRelation = "car")
class Car2 implements Identifiable<Integer> {
    private final Integer id;
    private final Integer year;
    private final String manufacturer;
    private final String model;
    private final Boolean verified;
    private final String dlc;

    private static final AtomicInteger idCounter = new AtomicInteger(0);

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

    public Integer getYear() {
        return year;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public Boolean getVerified() {
        return verified;
    }

    public String getDlc() {
        return dlc;
    }
}
