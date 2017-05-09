package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

class Event extends ResourceSupport {
    @JsonProperty("id")
    private final Integer eventId;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("tier")
    private final Integer tier;
    @JsonProperty("cars")
    private final Set<Car> cars;
    @JsonProperty("rounds")
    private final List<Round> rounds;
    @JsonProperty("verified")
    private final Boolean verified;

    private final static AtomicInteger id = new AtomicInteger(0);

    @JsonCreator
    public Event(
            @JsonProperty("name") String name,
            @JsonProperty("tier") Integer tier,
            @JsonProperty("rounds") JsonNode rounds,
            @JsonProperty("verified") Boolean verified,
            @JsonProperty("carFilter") JsonNode carFilter) {
        this.eventId = id.incrementAndGet();
        this.name = name;
        this.tier = tier;

        if (carFilter.isNull()) {
            this.cars = null;
        } else {
            Set<Car> cars = new HashSet<>(Application.CARS);
            List<CarFilter> carFilters = Application.getData(CarFilter.class, carFilter);
            carFilters.forEach(filter -> cars.removeIf(filter.getOperation().negate()));
            this.cars = cars;
        }

        this.verified = verified;

        Round.resetId();
        this.rounds = Application.getData(Round.class, rounds);

        this.add(new Link(String.format("/events/%s", eventId.toString())).withSelfRel());
    }

    Integer getEventId() {
        return eventId;
    }

    Set<Car> getCars() {
        return cars;
    }

    List<Round> getRounds() {
        return rounds;
    }

    static Optional<Event> getEventByID(Integer eventId) {
        return Optional.ofNullable(Application.EVENTS.stream()
                .filter(event -> event.getEventId().equals(eventId))
                .findAny()
                .orElse(null));
    }

}
