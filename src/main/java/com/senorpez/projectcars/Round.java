package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

class Round extends ResourceSupport {
    @JsonProperty("id")
    private final Integer roundId;
    private final Track track;
    private final List<Race> races;

    static final List<String> DB_COLUMNS = Arrays.asList(
            "id",
            "eventID",
            "trackID"
    );
    static final String DB_TABLE_NAME = "rounds";

    Round(ResultSet roundResults, Track track, List<Race> races) throws SQLException {
        this.roundId = roundResults.getInt("id");

        this.track = track;
        this.races = races;
    }

    public Integer getRoundId() {
        return roundId;
    }

    public Track getTrack() {
        return track;
    }

    public List<Race> getRaces() {
        return races;
    }
}
