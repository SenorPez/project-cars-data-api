package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Relation(value = "event", collectionRelation = "event")
class Event implements Identifiable<Integer> {
    @JsonProperty("id")
    private final Integer id;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("tier")
    private final Integer tier;
    @JsonProperty("cars")
    private final Set<Car> cars;
    @JsonProperty("rounds")
    private final Set<Round> rounds;
    @JsonProperty("verified")
    private final Boolean verified;

    private final static AtomicInteger eventId = new AtomicInteger(0);

    @JsonCreator
    public Event(
            @JsonProperty("name") String name,
            @JsonProperty("tier") Integer tier,
            @JsonProperty("rounds") JsonNode rounds,
            @JsonProperty("verified") Boolean verified,
            @JsonProperty("carFilter") JsonNode carFilter) {
        this.id = eventId.incrementAndGet();
        this.name = name;
        this.tier = tier;

        if (carFilter.isNull()) {
            this.cars = null;
        } else {
            Set<Car> cars = new HashSet<>(Application.CARS);
            Set<CarFilter> carFilters = Application.getProjectCarsData(CarFilter.class, carFilter);
            carFilters.forEach(filter -> cars.removeIf(filter.getOperation().negate()));
            this.cars = cars;
        }

        this.verified = verified;

        Round.resetId();
        this.rounds = Application.getProjectCarsData(Round.class, rounds);
    }

    @Override
    public Integer getId() {
        return id;
    }

    String getName() {
        return name;
    }

    Integer getTier() {
        return tier;
    }

    Boolean isVerified() {
        return verified;
    }

    Set<Car> getCars() {
        return cars;
    }

    Set<Round> getRounds() {
        return rounds;
    }
}
