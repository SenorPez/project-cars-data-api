package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

class Event {
    private final Integer id;
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

    Event(Integer id, String name, Integer tier, List<Car> cars, List<Round> rounds, String carFilter, Boolean verified) {
        this.id = id;
        this.name = name;
        this.tier = tier;
        this.cars = cars;
        this.rounds = rounds;
        this.verified = verified;

        this.carFilter = carFilter;
    }

    Event(ResultSet eventResults, List<Car> cars, List<Round> rounds) throws SQLException {
        this.id = eventResults.getInt("id");
        this.name = eventResults.getString("name");

        Integer tier = eventResults.getInt("tier");
        this.tier = eventResults.wasNull() ? null : tier;

        this.verified = eventResults.getBoolean("verified");

        this.carFilter = eventResults.getString("carFilter");

        this.cars = cars;
        this.rounds = rounds;
    }

    public Integer getId() {
        return id;
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
