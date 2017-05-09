package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.UriTemplate;

import java.util.HashMap;
import java.util.Map;

class Car extends ResourceSupport {
    private static final UriTemplate template = new UriTemplate("/v1/cars/{carId}");
    private final Map<String, String> uriVariables = new HashMap<>();

    @JsonProperty("id")
    private final Integer carId;
    @JsonProperty("manufacturer")
    private final String manufacturer;
    @JsonProperty("model")
    private final String model;
    @JsonProperty("country")
    private final String country;
    @JsonProperty("carClass")
    private final String carClass;
    @JsonProperty("year")
    private final Integer year;
    @JsonProperty("drivetrain")
    private final Drivetrain drivetrain;
    @JsonProperty("enginePosition")
    private final EnginePosition enginePosition;
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
    private final ShiftPattern shiftPattern;
    @JsonProperty("shifter")
    private final Shifter shifter;
    @JsonProperty("gears")
    private final Integer gears;
    @JsonProperty("dlc")
    private final String dlc;

    enum Drivetrain {
        FWD,
        RWD,
        AWD
    }

    enum EnginePosition {
        FRONT ("Front"),
        MID ("Mid"),
        REAR ("Rear");

        private final String displayString;

        EnginePosition(String displayString) {
            this.displayString = displayString;
        }

        @JsonValue
        public String getDisplayString() {
            return displayString;
        }
    }

    enum ShiftPattern {
        SEQUENTIAL ("Sequential"),
        H ("H");

        private final String displayString;

        ShiftPattern(String displayString) { this.displayString = displayString; }

        @JsonValue
        public String getDisplayString() { return displayString; }
    }

    enum Shifter {
        SHIFTER ("Shifter"),
        PADDLES ("Paddles");

        private final String displayString;

        Shifter(String displayString) { this.displayString = displayString; }

        @JsonValue
        public String getDisplayString() { return displayString; }
    }

    @JsonCreator
    public Car(
            @JsonProperty("id") Integer carId,
            @JsonProperty("manufacturer") String manufacturer,
            @JsonProperty("model") String model,
            @JsonProperty("country") String country,
            @JsonProperty("class") String carClass,
            @JsonProperty("year") Integer year,
            @JsonProperty("drivetrain") Drivetrain drivetrain,
            @JsonProperty("enginePosition") EnginePosition enginePosition,
            @JsonProperty("engineType") String engineType,
            @JsonProperty("topSpeed") Integer topSpeed,
            @JsonProperty("horsepower") Integer horsepower,
            @JsonProperty("acceleration") Float acceleration,
            @JsonProperty("braking") Float braking,
            @JsonProperty("weight") Integer weight,
            @JsonProperty("torque") Integer torque,
            @JsonProperty("weightBalance") Integer weightBalance,
            @JsonProperty("wheelbase") Float wheelbase,
            @JsonProperty("shiftPattern") ShiftPattern shiftPattern,
            @JsonProperty("shifter") Shifter shifter,
            @JsonProperty("gears") Integer gears,
            @JsonProperty("dlc") String dlc) {
        this.carId = carId;
        this.manufacturer = manufacturer;
        this.model = model;
        this.country = country;
        this.carClass = carClass;
        this.year = year;
        this.drivetrain = drivetrain;
        this.enginePosition = enginePosition;
        this.engineType = engineType;
        this.topSpeed = topSpeed;
        this.horsepower = horsepower;
        this.acceleration = acceleration;
        this.braking = braking;
        this.weight = weight;
        this.torque = torque;
        this.weightBalance = weightBalance;
        this.wheelbase = wheelbase;
        this.shiftPattern = shiftPattern;
        this.shifter = shifter;
        this.gears = gears;
        this.dlc = dlc;

        this.add(new Link(String.format("/cars/%s", carId.toString())).withSelfRel());
    }

    Integer getCarId() {
        return carId;
    }
}