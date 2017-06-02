package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

import java.util.concurrent.atomic.AtomicInteger;

@Relation(value = "race", collectionRelation = "race")
class Race implements Identifiable<Integer> {
    @JsonProperty("raceId")
    private final Integer id;
    @JsonProperty("laps")
    private final Integer laps;
    @JsonProperty("time")
    private final Integer time;
    @JsonProperty("type")
    private final String type;

    private final static AtomicInteger raceId = new AtomicInteger(0);

    @JsonCreator
    public Race(
            @JsonProperty("laps") Integer laps,
            @JsonProperty("time") Integer time,
            @JsonProperty("type") String type) {
        this.id = raceId.incrementAndGet();
        this.laps = laps;
        this.time = time;
        this.type = type;
    }

    @Override
    public Integer getId() {
        return id;
    }

    static void resetId() {
        raceId.set(0);
    }
}
