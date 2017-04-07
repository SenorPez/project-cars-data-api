package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

class Event {
    private final Integer id;
    private final String name;
    private final List<Car> cars;
    private final List<Round> rounds;

    @JsonIgnore
    private final String carFilter;

    Event(Integer id, String name, List<Car> cars, List<Round> rounds, String carFilter) {
        this.id = id;
        this.name = name;
        this.cars = cars;
        this.rounds = rounds;

        this.carFilter = carFilter;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
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
}
