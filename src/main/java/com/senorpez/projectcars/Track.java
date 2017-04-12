package com.senorpez.projectcars;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Track {
    private final Integer id;
    private final String name;
    private final String location;
    private final String variation;
    private final Float length;
    private final List<Float> pitEntry;
    private final List<Float> pitExit;
    private final Integer gridSize;

    Track(Integer id, String name, String location, String variation, Float length, Float pitEntryX, Float pitEntryZ, Float pitExitX, Float pitExitZ, Integer gridSize) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.variation = variation;
        this.length = length;

        this.pitEntry = pitEntryX == null || pitEntryZ == null
                ? null
                : Collections.unmodifiableList(Stream.of(pitEntryX, pitEntryZ).collect(Collectors.toList()));

        this.pitExit = pitExitX == null || pitExitZ == null
                ? null
                : Collections.unmodifiableList(Stream.of(pitExitX, pitExitZ).collect(Collectors.toList()));
        this.gridSize = gridSize;
    }

    public Integer getId() {
        return id;
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
