package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import java.util.concurrent.atomic.AtomicInteger;

class Race extends ResourceSupport {
    @JsonProperty("id")
    private final Integer raceId;
    @JsonProperty("laps")
    private final Integer laps;
    @JsonProperty("time")
    private final Integer time;
    @JsonProperty("type")
    private final String type;

    private final static AtomicInteger id = new AtomicInteger(0);

    @JsonCreator
    public Race(
            @JsonProperty("laps") Integer laps,
            @JsonProperty("time") Integer time,
            @JsonProperty("type") String type) {
        this.raceId = id.incrementAndGet();
        this.laps = laps;
        this.time = time;
        this.type = type;
    }

    Integer getRaceId() {
        return raceId;
    }

    static void resetId() {
        id.set(0);
    }
}
