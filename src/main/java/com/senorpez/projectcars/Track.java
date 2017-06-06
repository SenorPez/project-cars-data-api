package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Relation(value = "track", collectionRelation = "track")
class Track implements Identifiable<Integer> {
    @JsonProperty("id")
    private final Integer id;
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
        this.id = trackId;
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
    }

    @Override
    public Integer getId() {
        return id;
    }

    String getName() {
        return name;
    }

    String getLocation() {
        return location;
    }

    String getVariation() {
        return variation;
    }

    public Float getLength() {
        return length;
    }

    public List<Float> getPitEntry() {
        return pitEntry;
    }

    public List<Float> getPitExit() {
        return pitExit;
    }

    public Integer getGridSize() {
        return gridSize;
    }
}
