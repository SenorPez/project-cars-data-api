package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonValue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

class Car {
    private final Integer id;
    private final String manufacturer;
    private final String model;
    private final String country;
    private final String carClass;
    private final Integer year;
    private final Drivetrain drivetrain;

    private final EnginePosition enginePosition;

    static final List<String> DB_COLUMNS = Arrays.asList(
            "id",
            "manufacturer",
            "model",
            "class",
            "year",
            "country",
            "drivetrain",
            "enginePosition"
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

    Car(Integer id, String manufacturer, String model, String country, String carClass, Integer year, Drivetrain drivetrain, EnginePosition enginePosition) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.country = country;
        this.carClass = carClass;
        this.year = year;
        this.drivetrain = drivetrain;
        this.enginePosition = enginePosition;
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
}