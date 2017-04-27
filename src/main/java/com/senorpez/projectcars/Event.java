package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

class Event extends ResourceSupport {
    @JsonProperty("id")
    private final Integer eventId;
    private final String name;
    private final Integer tier;
    private final List<Car> cars;
    private final List<Round> rounds;
    private final Boolean verified;

    final static List<String> DB_COLUMNS = Arrays.asList(
            "id",
            "name",
            "carFilter",
            "tier",
            "verified"
    );
    final static String DB_TABLE_NAME = "events";

    @JsonIgnore
    private final String carFilter;

    Event(ResultSet eventResults, List<Car> cars, List<Round> rounds) throws SQLException {
        this.eventId = eventResults.getInt("id");
        this.name = eventResults.getString("name");

        Integer tier = eventResults.getInt("tier");
        this.tier = eventResults.wasNull() ? null : tier;

        this.verified = eventResults.getBoolean("verified");

        this.carFilter = eventResults.getString("carFilter");

        this.cars = cars;
        this.rounds = rounds;
    }

    public Integer getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public Integer getTier() {
        return tier;
    }

    public List<Car> getCars() {
        return cars;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public String getCarFilter() {
        return carFilter;
    }

    public Boolean getVerified() {
        return verified;
    }
}
