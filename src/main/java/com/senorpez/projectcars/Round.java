package com.senorpez.projectcars;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

class Round {
    private final Integer id;
    private final Track track;
    private final List<Race> races;

    static final List<String> DB_COLUMNS = Arrays.asList(
            "id",
            "eventID",
            "trackID"
    );
    static final String DB_TABLE_NAME = "rounds";

    Round(Integer id, Track track, List<Race> races) {
        this.id = id;
        this.track = track;
        this.races = races;
    }

    Round(ResultSet roundResults, Track track, List<Race> races) throws SQLException {
        this.id = roundResults.getInt("id");

        this.track = track;
        this.races = races;
    }

    public Integer getId() {
        return id;
    }

    public Track getTrack() {
        return track;
    }

    public List<Race> getRaces() {
        return races;
    }
}
