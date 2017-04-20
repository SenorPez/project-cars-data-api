package com.senorpez.projectcars;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Race {
    private final Integer id;
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

    Race(Integer id, Integer laps, Integer time, String type) {
        this.id = id;
        this.laps = laps;
        this.time = time;
        this.type = type;
    }

    Race(ResultSet raceResults) throws SQLException {
        this.id = raceResults.getInt("id");

        Integer laps = raceResults.getInt("laps");
        this.laps = (raceResults.wasNull()) ? null : laps;

        Integer time = raceResults.getInt("time");
        this.time = (raceResults.wasNull()) ? null : time;

        String type = raceResults.getString("type");
        this.type = (raceResults.wasNull()) ? null : type;
    }

    public Integer getId() {
        return id;
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
