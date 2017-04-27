package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.hateoas.ResourceSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

class Car extends ResourceSupport {
    @JsonProperty("id")
    private final Integer carId;
    private final String manufacturer;
    private final String model;
    private final String country;
    private final String carClass;
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

    static final List<String> DB_COLUMNS = Arrays.asList(
            "id",
            "manufacturer",
            "model",
            "class",
            "year",
            "country",
            "drivetrain",
            "enginePosition",
            "engineType",
            "topSpeed",
            "horsepower",
            "acceleration",
            "braking",
            "weight",
            "torque",
            "weightBalance",
            "wheelbase",
            "shiftPattern",
            "shifter",
            "gears",
            "dlc"
    );
    static final String DB_TABLE_NAME = "cars";

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

    Car(ResultSet carResults) throws SQLException {
        this.carId = carResults.getInt("id");
        this.manufacturer = carResults.getString("manufacturer");
        this.model = carResults.getString("model");
        this.country = carResults.getString("country");
        this.carClass = carResults.getString("class");
        this.year = carResults.getInt("year");
        this.drivetrain = Drivetrain.valueOf(carResults.getString("drivetrain").toUpperCase());
        this.enginePosition = EnginePosition.valueOf(carResults.getString("enginePosition").toUpperCase());
        this.engineType = carResults.getString("engineType");
        this.topSpeed = carResults.getInt("topSpeed");
        this.horsepower = carResults.getInt("horsepower");
        this.acceleration = carResults.getFloat("acceleration");
        this.braking = carResults.getFloat("braking");
        this.weight = carResults.getInt("weight");
        this.torque = carResults.getInt("torque");
        this.weightBalance = carResults.getInt("weightBalance");
        this.wheelbase = carResults.getFloat("wheelbase");
        this.shiftPattern = ShiftPattern.valueOf(carResults.getString("shiftPattern").toUpperCase());
        this.shifter = Shifter.valueOf(carResults.getString("shifter").toUpperCase());
        this.gears = carResults.getInt("gears");
        this.dlc = carResults.getString("dlc");
    }

    Integer getCarId() {
        return carId;
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

    public String getCarClass() {
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
}