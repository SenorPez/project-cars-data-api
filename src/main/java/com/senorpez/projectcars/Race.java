package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Race extends ResourceSupport {
    @JsonProperty("id")
    private final Integer raceId;
    private final Integer laps;
    private final Integer time;
    private final String type;

    static final List<String> DB_COLUMNS = Arrays.asList(
            "id",
            "roundID",
            "eventID",
            "laps",
            "time",
            "type"
    );
    static final String DB_TABLE_NAME = "races";

    Race(ResultSet raceResults) throws SQLException {
        this.raceId = raceResults.getInt("id");

        Integer laps = raceResults.getInt("laps");
        this.laps = (raceResults.wasNull()) ? null : laps;

        Integer time = raceResults.getInt("time");
        this.time = (raceResults.wasNull()) ? null : time;

        String type = raceResults.getString("type");
        this.type = (raceResults.wasNull()) ? null : type;
    }

    public Integer getRaceId() {
        return raceId;
    }

    public Integer getLaps() {
        return laps;
    }

    public Integer getTime() {
        return time;
    }

    public String getType() {
        return type;
    }
}
