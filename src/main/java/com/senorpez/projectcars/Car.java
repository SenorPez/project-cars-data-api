package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

import java.util.Set;

@Relation(value = "car", collectionRelation = "car")
class Car implements Identifiable<Integer> {
    @JsonProperty("id")
    private final Integer id;
    @JsonProperty("manufacturer")
    private final String manufacturer;
    @JsonProperty("model")
    private final String model;
    @JsonProperty("country")
    private final String country;
    @JsonProperty("carClass")
    private final CarClass carClass;
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
    @JsonProperty("liveries")
    private final Set<Livery> liveries;

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
            @JsonProperty("id") Integer id,
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
            @JsonProperty("dlc") String dlc,
            @JsonProperty("liveries")JsonNode liveries) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.country = country;
        this.carClass = Application.CAR_CLASSES.stream().filter(cclass -> cclass.getName().equalsIgnoreCase(carClass)).findAny().orElseThrow(RuntimeException::new);
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

        this.liveries = Application.getProjectCarsData(Livery.class, liveries);
    }

    @Override
    public Integer getId() {
        return id;
    }

    String getManufacturer() {
        return manufacturer;
    }

    String getModel() {
        return model;
    }

    String getCountry() {
        return country;
    }

    CarClass getCarClass() {
        return carClass;
    }

    Integer getYear() {
        return year;
    }

    Drivetrain getDrivetrain() {
        return drivetrain;
    }

    EnginePosition getEnginePosition() {
        return enginePosition;
    }

    String getEngineType() {
        return engineType;
    }

    Integer getTopSpeed() {
        return topSpeed;
    }

    Integer getHorsepower() {
        return horsepower;
    }

    Float getAcceleration() {
        return acceleration;
    }

    Float getBraking() {
        return braking;
    }

    Integer getWeight() {
        return weight;
    }

    Integer getTorque() {
        return torque;
    }

    Integer getWeightBalance() {
        return weightBalance;
    }

    Float getWheelbase() {
        return wheelbase;
    }

    ShiftPattern getShiftPattern() {
        return shiftPattern;
    }

    Shifter getShifter() {
        return shifter;
    }

    Integer getGears() {
        return gears;
    }

    String getDlc() {
        return dlc;
    }

    Set<Livery> getLiveries() {
        return liveries;
    }
}