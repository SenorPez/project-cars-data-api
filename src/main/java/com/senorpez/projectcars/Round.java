package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

class Round extends ResourceSupport {
    @JsonProperty("id")
    private final Integer roundId;
    @JsonProperty("track")
    private final Track track;
    @JsonProperty("races")
    private final Set<Race> races;

    private final static AtomicInteger id = new AtomicInteger(0);

    @JsonCreator
    public Round(
            @JsonProperty("location") String location,
            @JsonProperty("variation") String variation,
            @JsonProperty("races") JsonNode races) {
        this.roundId = id.incrementAndGet();
        this.track = Application.TRACKS.stream()
                .filter(foundTrack ->
                        foundTrack.getLocation().equalsIgnoreCase(location)
                        && foundTrack.getVariation().equalsIgnoreCase(variation))
                .findFirst()
                .orElse(null);

        Race.resetId();
        this.races = Application.getData(Race.class, races);
    }

    Integer getRoundId() {
        return roundId;
    }

    public Track getTrack() {
        return track;
    }

    Set<Race> getRaces() {
        return races;
    }

    static void resetId() {
        id.set(0);
    }

    static Optional<Round> getRoundById(Integer eventId, Integer roundId) {
        return Optional.ofNullable(Event.getEventByID(eventId).map(
                event -> event.getRounds().stream()
                        .filter(round -> round.getRoundId().equals(roundId))
                        .findAny()
                        .orElse(null))
                .orElse(null));
    }
}
