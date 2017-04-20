package com.senorpez.projectcars;

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

    static final List<String> DB_COLUMNS = Arrays.asList(
            "id",
            "manufacturer",
            "model",
            "country",
            "class",
            "year"
    );
    static final String DB_TABLE_NAME = "cars";

    Car(Integer id, String manufacturer, String model, String country, String carClass, Integer year) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.country = country;
        this.carClass = carClass;
        this.year = year;
    }

    Car(ResultSet carResults) throws SQLException {
        this.id = carResults.getInt("id");
        this.manufacturer = carResults.getString("manufacturer");
        this.model = carResults.getString("model");
        this.country = carResults.getString("country");
        this.carClass = carResults.getString("class");
        this.year = carResults.getInt("year");
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
}
