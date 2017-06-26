package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Identifiable;

class CarModel implements Identifiable<Integer> {
    @JsonProperty("id")
    private final Integer id;
    @JsonProperty("manufacturer")
    private final String manufacturer;
    @JsonProperty("model")
    private final String model;
    @JsonProperty("country")
    private final String country;
    @JsonProperty("year")
    private final Integer year;
    @JsonProperty("drivetrain")
    private final Car.Drivetrain drivetrain;
    @JsonProperty("enginePosition")
    private final Car.EnginePosition enginePosition;
    @JsonProperty("engineType")
    private final String engineType;
    @JsonProperty("topSpeed")
    private final Integer topSpeed;
    @JsonProperty("horsepower")
    private final Integer horsepower;
    @JsonProperty("acceleration")
    private final Float acceleration;
    @JsonProperty("braking")
    private final Float braking;
    @JsonProperty("weight")
    private final Integer weight;
    @JsonProperty("torque")
    private final Integer torque;
    @JsonProperty("weightBalance")
    private final Integer weightBalance;
    @JsonProperty("wheelbase")
    private final Float wheelbase;
    @JsonProperty("shiftPattern")
    private final Car.ShiftPattern shiftPattern;
    @JsonProperty("shifter")
    private final Car.Shifter shifter;
    @JsonProperty("gears")
    private final Integer gears;
    @JsonProperty("dlc")
    private final String dlc;

    CarModel(Car car) {
        this.id = car.getId();
        this.manufacturer = car.getManufacturer();
        this.model = car.getModel();
        this.country = car.getCountry();
        this.year = car.getYear();
        this.drivetrain = car.getDrivetrain();
        this.enginePosition = car.getEnginePosition();
        this.engineType = car.getEngineType();
        this.topSpeed = car.getTopSpeed();
        this.horsepower = car.getHorsepower();
        this.acceleration = car.getAcceleration();
        this.braking = car.getBraking();
        this.weight = car.getWeight();
        this.torque = car.getTorque();
        this.weightBalance = car.getWeightBalance();
        this.wheelbase = car.getWheelbase();
        this.shiftPattern = car.getShiftPattern();
        this.shifter = car.getShifter();
        this.gears = car.getGears();
        this.dlc = car.getDlc();
    }

    @Override
    public Integer getId() {
        return id;
    }
}
