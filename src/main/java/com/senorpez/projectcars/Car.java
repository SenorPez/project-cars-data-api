package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonValue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.round;

class Car {
    private final Integer id;
    private final String manufacturer;
    private final String model;
    private final String country;
    private final String carClass;
    private final Integer year;
    private final Drivetrain drivetrain;
    private final EnginePosition enginePosition;
    private final String engineType;

    private final Integer topSpeedMetric;
    private final Integer topSpeedImperial;

    private static final Double KM_TO_MI = 0.621371;

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
            "topSpeed"
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

        private String displayString;

        EnginePosition(String displayString) {
            this.displayString = displayString;
        }

        @JsonValue
        public String getDisplayString() {
            return displayString;
        }
    }

    Car(Integer id, String manufacturer, String model, String country, String carClass, Integer year, Drivetrain drivetrain, EnginePosition enginePosition, String engineType, Integer topSpeedMetric) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.country = country;
        this.carClass = carClass;
        this.year = year;
        this.drivetrain = drivetrain;
        this.enginePosition = enginePosition;
        this.engineType = engineType;

        this.topSpeedMetric = topSpeedMetric;
        this.topSpeedImperial = (int) round(topSpeedMetric * KM_TO_MI);
    }

    Car(ResultSet carResults) throws SQLException {
        this.id = carResults.getInt("id");
        this.manufacturer = carResults.getString("manufacturer");
        this.model = carResults.getString("model");
        this.country = carResults.getString("country");
        this.carClass = carResults.getString("class");
        this.year = carResults.getInt("year");
        this.drivetrain = Drivetrain.valueOf(carResults.getString("drivetrain").toUpperCase());
        this.enginePosition = EnginePosition.valueOf(carResults.getString("enginePosition").toUpperCase());
        this.engineType = carResults.getString("engineType");

        this.topSpeedMetric = carResults.getInt("topSpeed");
        this.topSpeedImperial = (int) round(this.topSpeedMetric * KM_TO_MI);
    }

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

    public Integer getTopSpeedMetric() {
        return topSpeedMetric;
    }

    public Integer getTopSpeedImperial() {
        return topSpeedImperial;
    }
}