package com.senorpez.projectcars;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class Track {
    private final Integer id;
    private final String name;
    private final String location;
    private final String variation;
    private final Float length;
    private final List<Float> pitEntry;
    private final List<Float> pitExit;
    private final Integer gridSize;

    final static List<String> DB_COLUMNS = Arrays.asList(
            "id",
            "name",
            "location",
            "variation",
            "length",
            "pitEntryX",
            "pitEntryZ",
            "pitExitX",
            "pitExitZ",
            "gridSize"
    );
    final static String DB_TABLE_NAME = "tracks";

    Track(Integer id, String name, String location, String variation, Float length, Float pitEntryX, Float pitEntryZ, Float pitExitX, Float pitExitZ, Integer gridSize) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.variation = variation;
        this.length = length;

        this.pitEntry = pitEntryX == null || pitEntryZ == null
                ? null
                : Collections.unmodifiableList(Arrays.asList(pitEntryX, pitEntryZ));

        this.pitExit = pitExitX == null || pitExitZ == null
                ? null
                : Collections.unmodifiableList(Arrays.asList(pitExitX, pitExitZ));
        this.gridSize = gridSize;
    }

    Track(ResultSet trackResults) throws SQLException {
        this.id = trackResults.getInt("id");
        this.name = trackResults.getString("name");
        this.location = trackResults.getString("location");
        this.variation = trackResults.getString("variation");
        this.length = trackResults.getFloat("length");

        Float pitEntryX = trackResults.getFloat("pitEntryX");
        if (trackResults.wasNull()) pitEntryX = null;
        Float pitEntryZ = trackResults.getFloat("pitEntryZ");
        if (trackResults.wasNull()) pitEntryZ = null;

        this.pitEntry = pitEntryX == null || pitEntryZ == null
                ? null
                : Collections.unmodifiableList(Arrays.asList(pitEntryX, pitEntryZ));

        Float pitExitX = trackResults.getFloat("pitExitX");
        if (trackResults.wasNull()) pitExitX = null;
        Float pitExitZ = trackResults.getFloat("pitExitZ");
        if (trackResults.wasNull()) pitExitZ = null;

        this.pitExit = pitExitX == null || pitExitZ == null
                ? null
                : Collections.unmodifiableList(Arrays.asList(pitExitX, pitExitZ));

        this.gridSize = trackResults.getInt("gridSize");
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
