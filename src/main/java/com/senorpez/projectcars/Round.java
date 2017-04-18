package com.senorpez.projectcars;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

class Round {
    private final Integer id;
    private final Track track;
    private final Integer laps;
    private final Integer time;

    static final List<String> DB_COLUMNS = Arrays.asList(
            "id",
            "eventID",
            "trackID",
            "laps",
            "time"
    );
    static final String DB_TABLE_NAME = "rounds";

    Round(Integer id, Track track, Integer laps, Integer time) {
        this.id = id;
        this.track = track;
        this.laps = laps;
        this.time = time;
    }

    Round(ResultSet roundResults, Track track) throws SQLException {
        this.id = roundResults.getInt("id");
        this.track = track;

        Integer laps = roundResults.getInt("laps");
        if (roundResults.wasNull()) laps = null;
        this.laps = laps;

        Integer time = roundResults.getInt("time");
        if (roundResults.wasNull()) time = null;
        this.time = time;
    }

    public Integer getId() {
        return id;
    }

    public Track getTrack() {
        return track;
    }

    public Integer getLaps() {
        return laps;
    }

    public Integer getTime() {
        return time;
    }
}
