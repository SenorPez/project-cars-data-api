package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class Track extends ResourceSupport {
    @JsonProperty("id")
    private final Integer trackId;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("location")
    private final String location;
    @JsonProperty("variation")
    private final String variation;
    @JsonProperty("length")
    private final Float length;
    @JsonProperty("pitEntry")
    private final List<Float> pitEntry;
    @JsonProperty("pitExit")
    private final List<Float> pitExit;
    @JsonProperty("gridSize")
    private final Integer gridSize;

    @JsonCreator
    public Track(
            @JsonProperty("id") Integer trackId,
            @JsonProperty("name") String name,
            @JsonProperty("location") String location,
            @JsonProperty("variation") String variation,
            @JsonProperty("length") Float length,
            @JsonProperty("pitEntryX") Float pitEntryX,
            @JsonProperty("pitEntryZ") Float pitEntryZ,
            @JsonProperty("pitExitX") Float pitExitX,
            @JsonProperty("pitExitZ") Float pitExitZ,
            @JsonProperty("gridSize") Integer gridSize) {
        this.trackId = trackId;
        this.name = name;
        this.location = location;
        this.variation = variation;
        this.length = length;

        this.pitEntry = pitEntryX == null || pitEntryZ == null
                ? null
                : Collections.unmodifiableList(Arrays.asList(pitEntryX, pitEntryZ));

        this.pitExit = pitExitX == null || pitEntryZ == null
                ? null
                : Collections.unmodifiableList(Arrays.asList(pitExitX, pitExitZ));

        this.gridSize = gridSize;

        this.add(new Link(String.format("/tracks/%s", trackId.toString())).withSelfRel());
    }

    Integer getTrackId() {
        return trackId;
    }

    String getLocation() {
        return location;
    }

    String getVariation() {
        return variation;
    }
}
