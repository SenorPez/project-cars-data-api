package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

import java.util.concurrent.atomic.AtomicInteger;

@Relation(value = "track", collectionRelation = "track")
class Track2 implements Identifiable<Integer> {
    private final Integer id;
    private final String country;
    private final String name;
    private final String location;
    private final String variation;
    private final Boolean verified;

    private static final AtomicInteger idCounter = new AtomicInteger(0);

    @JsonCreator
    public Track2(
            @JsonProperty("country") final String country,
            @JsonProperty("name") final String name,
            @JsonProperty("location") final String location,
            @JsonProperty("variation") final String variation,
            @JsonProperty("verified") final Boolean verified) {
        this.id = idCounter.getAndIncrement();
        this.country = country;
        this.name = name;
        this.location = location;
        this.variation = variation;
        this.verified = verified;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getVariation() {
        return variation;
    }

    public Boolean getVerified() {
        return verified;
    }
}
