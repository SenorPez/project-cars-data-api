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
    private final Integer id;
    private final String manufacturer;
    private final String model;
    private final String country;
    private final CarClass carClass;
    private final Integer year;
    private final Drivetrain drivetrain;
    private final EnginePosition enginePosition;
    private final String engineType;
    private final Integer topSpeed;
    private final Integer horsepower;
    private final Float acceleration;
    private final Float braking;
    private final Integer weight;
    private final Integer torque;
    private final Integer weightBalance;
    private final Float wheelbase;
    private final ShiftPattern shiftPattern;
    private final Shifter shifter;
    private final Integer gears;
    private final String dlc;
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

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getCountry() {
        return country;
    }

    public CarClass getCarClass() {
        return carClass;
    }

    public Integer getYear() {
        return year;
    }

    public Drivetrain getDrivetrain() {
        return drivetrain;
    }

    public EnginePosition getEnginePosition() {
        return enginePosition;
    }

    public String getEngineType() {
        return engineType;
    }

    public Integer getTopSpeed() {
        return topSpeed;
    }

    public Integer getHorsepower() {
        return horsepower;
    }

    public Float getAcceleration() {
        return acceleration;
    }

    public Float getBraking() {
        return braking;
    }

    public Integer getWeight() {
        return weight;
    }

    public Integer getTorque() {
        return torque;
    }

    public Integer getWeightBalance() {
        return weightBalance;
    }

    public Float getWheelbase() {
        return wheelbase;
    }

    public ShiftPattern getShiftPattern() {
        return shiftPattern;
    }

    public Shifter getShifter() {
        return shifter;
    }

    public Integer getGears() {
        return gears;
    }

    public String getDlc() {
        return dlc;
    }

    public Set<Livery> getLiveries() {
        return liveries;
    }

    public String getName() {
        return String.join(" ", manufacturer, model);
    }
}