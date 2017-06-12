package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Relation(value = "round", collectionRelation = "round")
class Round implements Identifiable<Integer> {
    @JsonProperty("eventId")
    private final Integer id;
    @JsonProperty("track")
    private final Track track;
    @JsonProperty("races")
    private final Set<Race> races;

    private final static AtomicInteger eventId = new AtomicInteger(0);

    @JsonCreator
    public Round(
            @JsonProperty("location") String location,
            @JsonProperty("variation") String variation,
            @JsonProperty("races") JsonNode races) {
        this.id = eventId.incrementAndGet();
        this.track = Application.TRACKS.stream()
                .filter(foundTrack ->
                        foundTrack.getLocation().equalsIgnoreCase(location)
                                && foundTrack.getVariation().equalsIgnoreCase(variation))
                .findFirst()
                .orElseThrow(() -> new TrackNotFoundAPIException(location, variation));

        Race.resetId();
        this.races = Application.getProjectCarsData(Race.class, races);
    }

    @Override
    public Integer getId() {
        return id;
    }

    Track getTrack() {
        return track;
    }

    Set<Race> getRaces() {
        return races;
    }

    static void resetId() {
        eventId.set(0);
    }
}
