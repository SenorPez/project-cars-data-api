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
    private final Integer id;
    private final String name;
    private final Integer tier;
    private final Set<Car> cars;
    private final Set<Round> rounds;
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

    public String getName() {
        return name;
    }

    public Integer getTier() {
        return tier;
    }

    public Set<Car> getCars() {
        return cars;
    }

    public Set<Round> getRounds() {
        return rounds;
    }

    public Boolean getVerified() {
        return verified;
    }
}
